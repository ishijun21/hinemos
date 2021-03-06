/*

Copyright (C) 2006 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.util.apllog;


import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.clustercontrol.bean.HinemosModuleConstant;
import com.clustercontrol.bean.PriorityConstant;
import com.clustercontrol.commons.util.JpaTransactionManager;
import com.clustercontrol.fault.HinemosException;
import com.clustercontrol.fault.HinemosUnknown;
import com.clustercontrol.fault.InvalidRole;
import com.clustercontrol.maintenance.util.HinemosPropertyUtil;
import com.clustercontrol.monitor.bean.EventConfirmConstant;
import com.clustercontrol.notify.bean.OutputBasicInfo;
import com.clustercontrol.notify.session.NotifyControllerBean;
import com.clustercontrol.notify.util.NotifyUtil;
import com.clustercontrol.notify.util.SendSyslog;
import com.clustercontrol.util.CommandCreator;
import com.clustercontrol.util.CommandCreator.PlatformType;
import com.clustercontrol.util.CommandExecutor;
import com.clustercontrol.util.CommandExecutor.CommandResult;
import com.clustercontrol.util.HinemosMessage;
import com.clustercontrol.util.HinemosTime;
import com.clustercontrol.util.MessageConstant;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.StringBinder;

/**
 *
 * Hinemosの内部ログ（HinemosApp.log）の出力を行うクラス<BR>
 *
 * Hinemos内部で発生する事象をログやHinemosのイベントとして
 * 処理します。
 *
 */
public class AplLogger {

	private static final String INTERNAL_SCOPE="INTERNAL";
	private static final String INTERNAL_SCOPE_TEXT="Hinemos_Internal";

	private static final String PRIORITY_INFO = "info";
	private static final String PRIORITY_WARNING = "warning";
	private static final String PRIORITY_CRITICAL = "critical";
	private static final String MONITOR_ID = HinemosModuleConstant.SYSYTEM;

	private static ConcurrentHashMap<Long, Boolean> m_isInternalMode = new ConcurrentHashMap<Long, Boolean>();
	private static Log FILE_LOGGER = LogFactory.getLog("HinemosInternal");
	private static Log log = LogFactory.getLog(AplLogger.class);

	/**
	 *
	 * ログを出力します。<BR>
	 * 
	 * @param priority		優先度
	 * @param pluginId		プラグインID
	 * @param msgArgs		メッセージ置換項目
	 * @since
	 */
	public static void put(Integer priority, String pluginId, MessageConstant msgCode, Object[] msgArgs) {
		put(priority, pluginId, msgCode, msgArgs, null);
	}

	/**
	 *
	 * ログを出力します。<BR>
	 *
	 * @param priority		優先度
	 * @param pluginId		プラグインID
	 * @param msgArgs		メッセージ置換項目
	 * @param detailMsg		詳細メッセージ
	 * @since
	 */
	public static void put(Integer priority, String pluginId, MessageConstant msgCode, Object[] msgArgs, String detailMsg) {
		// 既にINTERNALモードに入っている場合はこれ以上出力しない
		Boolean isInternalMode = m_isInternalMode.get(Thread.currentThread().getId());
		if(isInternalMode != null && isInternalMode){
			log.debug("AplLogger has been already in INTERNAL mode. No INTERNAL logs are outputted. ");
			return;
		}
		try{
			// INTERNALモードに入る
			log.debug("AplLogger INTERNAL mode start");
			m_isInternalMode.put(Thread.currentThread().getId(), true);

			//現在日時取得
			Date nowDate = HinemosTime.getDateInstance();

			//メッセージ情報作成
			OutputBasicInfo output = new OutputBasicInfo();
			output.setPluginId(pluginId);
			output.setMonitorId(MONITOR_ID);
			output.setFacilityId(INTERNAL_SCOPE);
			output.setScopeText(INTERNAL_SCOPE_TEXT);
			output.setApplication(Messages.getString(HinemosModuleConstant.nameToMessageCode(pluginId), NotifyUtil.getNotifyLocale()));
			output.setMessage(msgCode.getMessage((String[]) msgArgs));
			output.setMessageOrg(detailMsg == null ? "":detailMsg);
			output.setPriority(priority);
			if (nowDate != null) {
				output.setGenerationDate(nowDate.getTime());
			}

			/////
			// 設定値取得(internal.event)
			////
			boolean isEvent = HinemosPropertyUtil.getHinemosPropertyBool("internal.event", true);
			int eventLevel = getPriority(HinemosPropertyUtil.getHinemosPropertyStr("internal.event.priority", PRIORITY_INFO));

			if(isEvent && isOutput(eventLevel, priority)){
				putEvent(output);
			}

			/////
			// 設定値取得(internal.file)
			////
			boolean isFile = HinemosPropertyUtil.getHinemosPropertyBool("internal.file", true);
			int fileLevel = getPriority(HinemosPropertyUtil.getHinemosPropertyStr("internal.file.priority", PRIORITY_INFO));

			if(isFile && isOutput(fileLevel, priority)){
				putFile(output);
			}

			/////
			// 設定値取得(internal.syslog)
			////
			boolean isSyslog = HinemosPropertyUtil.getHinemosPropertyBool("internal.syslog", false);
			int syslogLevel = getPriority(HinemosPropertyUtil.getHinemosPropertyStr("internal.syslog.priority", PRIORITY_INFO));
			if (isSyslog && isOutput(syslogLevel, priority)){
				putSyslog(output);
			}

			/////
			// 設定値取得(internal.mail)
			////
			boolean isMail = HinemosPropertyUtil.getHinemosPropertyBool("internal.mail", false);
			int mailLevel = getPriority(HinemosPropertyUtil.getHinemosPropertyStr(
					"internal.mail.priority", PRIORITY_INFO));
			if (isMail && isOutput(mailLevel, priority)) {
				putMail(output);
			}

			/////
			// 設定値取得(internal.command)
			////
			boolean isCommand = HinemosPropertyUtil.getHinemosPropertyBool("internal.command", false);
			int commandLevel = getPriority(HinemosPropertyUtil.getHinemosPropertyStr("internal.command.priority", PRIORITY_INFO));

			if (isCommand && isOutput(commandLevel, priority)) {
				putCommand(output);
			}
		}finally{
			// INTERNALモードから出る
			m_isInternalMode.put(Thread.currentThread().getId(), false);
			log.debug("AplLogger INTERNAL mode end");
		}
	}

	private static void putFile(OutputBasicInfo notifyInfo) {
		/**	ログファイル出力用フォーマット「日付  プラグインID,アプリケーション,監視項目ID,メッセージID,ファシリティID,メッセージ,詳細メッセージ」 */
		MessageFormat logfmt = new MessageFormat("{0,date,yyyy/MM/dd HH:mm:ss}  {1},{2},{3},{4},{5},{6}");
		// Locale取得
		Locale locale = NotifyUtil.getNotifyLocale();
		//メッセージを編集
		Object[] args ={notifyInfo.getGenerationDate(),notifyInfo.getPluginId(), notifyInfo.getApplication(),
				notifyInfo.getMonitorId(), notifyInfo.getPriority(),
				HinemosMessage.replace(notifyInfo.getMessage(), locale), notifyInfo.getMessageOrg()};
		String logmsg = logfmt.format(args);
		//ファイル出力
		log.debug("putFile() logmsg = " + logmsg);
		FILE_LOGGER.info(logmsg);
	}

	private static void putSyslog(OutputBasicInfo notifyInfo) {
		/**	syslog出力用フォーマット
		 * 「日付  プラグインID,アプリケーション,監視項目ID,ファシリティID,メッセージ,詳細メッセージ」 */
		MessageFormat syslogfmt = new MessageFormat("hinemos: {0},{1},{2},{3},{4}");

		// メッセージを編集
		Locale locale = NotifyUtil.getNotifyLocale();
		String priorityStr = Messages.getString(PriorityConstant.typeToMessageCode(notifyInfo.getPriority()), locale);
		Object[] args ={notifyInfo.getPluginId(), notifyInfo.getApplication(), notifyInfo.getMonitorId(),
				priorityStr, HinemosMessage.replace(notifyInfo.getMessage(), locale), notifyInfo.getMessageOrg()};
		String logmsg = syslogfmt.format(args);

		// 送信時刻をセット
		SimpleDateFormat sdf = new SimpleDateFormat(SendSyslog.HEADER_DATE_FORMAT, Locale.US);
		sdf.setTimeZone(HinemosTime.getTimeZone());
		String timeStamp = sdf.format(HinemosTime.getDateInstance());

		/////
		// 設定値取得(internal.syslog)
		////
		String hosts = HinemosPropertyUtil.getHinemosPropertyStr("internal.syslog.host", "192.168.1.1,192.168.1.2");
		String[] syslogHostList = hosts.split(",");
		int syslogPort = HinemosPropertyUtil.getHinemosPropertyNum("internal.syslog.port", Long.valueOf(514)).intValue();
		String syslogFacility = HinemosPropertyUtil.getHinemosPropertyStr("internal.syslog.facility", "daemon");
		String syslogSeverity = HinemosPropertyUtil.getHinemosPropertyStr("internal.syslog.severity", "alert");

		for (String syslogHost : syslogHostList) {
			log.debug("putSyslog() syslogHost = " + syslogHost + ", syslogPort = " + syslogPort +
					", syslogFacility = " + syslogFacility + ", syslogSeverity = " + syslogSeverity +
					", logmsg = " + logmsg + ", timeStamp = " + timeStamp);

			try {
				new NotifyControllerBean().sendAfterConvertHostname(syslogHost, syslogPort, syslogFacility,
						syslogSeverity, INTERNAL_SCOPE, logmsg, timeStamp);
			} catch (InvalidRole e) {
				log.warn("fail putSyslog monitorId=" + notifyInfo.getMonitorId() + ", message=" + notifyInfo.getMessage());
			} catch (HinemosUnknown e) {
				log.warn("fail putSyslog monitorId=" + notifyInfo.getMonitorId() + ", message=" + notifyInfo.getMessage());
			}
		}
	}

	private static boolean putEvent(OutputBasicInfo notifyInfo) {
		JpaTransactionManager jtm = null;
		
		try {
			// rollbackするとイベントが出力されなくなるため、rollback用のコールバックメソッドを登録する 
			jtm = new JpaTransactionManager(); 
			jtm.begin(); 
			
			jtm.addCallback(new AplLoggerPutEventAfterRollbackCallback(notifyInfo)); 
			
			jtm.commit(); 
			
			new NotifyControllerBean().insertEventLog(notifyInfo, EventConfirmConstant.TYPE_UNCONFIRMED);
			return true;
		} catch (HinemosUnknown e) {
			log.warn("fail putEvent monitorId=" + notifyInfo.getMonitorId() + ", message=" + notifyInfo.getMessage());
			return false;
		} catch (InvalidRole e) {
			log.warn("fail putEvent monitorId=" + notifyInfo.getMonitorId() + ", message=" + notifyInfo.getMessage());
			return false;
		} finally {
			if (jtm != null)
				jtm.close();
		}
	}

	private static void putMail(OutputBasicInfo notifyInfo) {
		// メール通知（デフォルトテンプレート）
		try {
			String addr = HinemosPropertyUtil.getHinemosPropertyStr("internal.mail.address", "user1@host.domain,user2@host.domain");
			String[] mailAddress = addr.split(",");
			new NotifyControllerBean().sendMail(mailAddress, notifyInfo);
		} catch (InvalidRole | HinemosUnknown | RuntimeException e) {
			log.warn("fail putMail monitorId=" + notifyInfo.getMonitorId() + ", message=" + notifyInfo.getMessage());
		}
	}

	private static void putCommand(OutputBasicInfo notifyInfo) {
		// コマンド通知
		try {
			String commandUser = HinemosPropertyUtil.getHinemosPropertyStr("internal.command.user", "root");
			String commandLine = HinemosPropertyUtil.getHinemosPropertyStr(
					"internal.command.commandline",
					"echo #[GENERATION_DATE] #[MESSAGE] >> /tmp/test.txt");
			int commandTimeout = 0;
			try {
				commandTimeout = HinemosPropertyUtil.getHinemosPropertyNum("internal.command.timeout", Long.valueOf(15000)).intValue();
			} catch (Exception e) {}

			Map<String, String> param = NotifyUtil.createParameter(notifyInfo, null);
			StringBinder binder = new StringBinder(param);
			String command = binder.bindParam(commandLine);

			log.info("excuting command. (effectiveUser = " + commandUser + ", command = " + command + ", mode = " + PlatformType.UNIX + ", timeout = " + commandTimeout + ")");
			String[] cmd = CommandCreator.createCommand(commandUser, command, PlatformType.UNIX);
			CommandExecutor cmdExec = new CommandExecutor(cmd, commandTimeout);
			cmdExec.execute();
			CommandResult ret = cmdExec.getResult();

			if (ret != null) {
				log.info("executed command. (exitCode = " + ret.exitCode + ", stdout = " + ret.stdout + ", stderr = " + ret.stderr + ")");
			}
		} catch (HinemosException | RuntimeException e) {
			log.warn("fail putCommand monitorId=" + notifyInfo.getMonitorId() + ", message=" + notifyInfo.getMessage());
			return;
		}
	}

	/**
	 *
	 * 文字列から、Priority区分を取得します。<BR>
	 *
	 * @param priority
	 * @since
	 */
	private static int getPriority(String priority) {
		int ret = PriorityConstant.TYPE_UNKNOWN;
		if(priority.equals(PRIORITY_CRITICAL)){
			ret = PriorityConstant.TYPE_CRITICAL;
		}else if(priority.equals(PRIORITY_WARNING)){
			ret = PriorityConstant.TYPE_WARNING;
		}else if(priority.equals(PRIORITY_INFO)){
			ret = PriorityConstant.TYPE_INFO;
		}
		return ret;
	}

	/**
	 * 送信を行うかのPriority毎の判定を行う
	 */
	private static boolean isOutput(int level, int priority){
		if (priority == PriorityConstant.TYPE_CRITICAL) {
			if (level == PriorityConstant.TYPE_CRITICAL ||
					level == PriorityConstant.TYPE_UNKNOWN ||
					level == PriorityConstant.TYPE_WARNING ||
					level == PriorityConstant.TYPE_INFO) {
				return true;
			} else {
				return false;
			}
		}
		if (priority == PriorityConstant.TYPE_UNKNOWN) {
			if (level == PriorityConstant.TYPE_UNKNOWN ||
					level == PriorityConstant.TYPE_WARNING ||
					level == PriorityConstant.TYPE_INFO) {
				return true;
			} else {
				return false;
			}
		}
		if (priority == PriorityConstant.TYPE_WARNING) {
			if (level == PriorityConstant.TYPE_WARNING ||
					level == PriorityConstant.TYPE_INFO) {
				return true;
			} else {
				return false;
			}
		}
		if (priority == PriorityConstant.TYPE_INFO) {
			if (level == PriorityConstant.TYPE_INFO) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
}
