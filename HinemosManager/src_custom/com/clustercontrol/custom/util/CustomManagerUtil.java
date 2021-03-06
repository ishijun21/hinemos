/*

Copyright (C) 2014 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.custom.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.clustercontrol.hinemosagent.bean.TopicInfo;
import com.clustercontrol.hinemosagent.util.AgentConnectUtil;

/**
 * マネージャ内で利用されるコマンド監視機能のユーティリティクラス<br/>
 * 
 * @since 4.0
 */
public class CustomManagerUtil {

	private static Log log = LogFactory.getLog(CustomManagerUtil.class);

	/**
	 * 接続されている全エージェントに対して、コマンド監視設定変更を通知する<br/>
	 */
	public static void broadcastConfigured() {
		// Local Variables
		TopicInfo topicInfo = null;

		// MAIN
		log.info("broadcasting custom monitor configuration modified.");

		topicInfo = new TopicInfo();
		topicInfo.setCustomMonitorChanged(true);

		AgentConnectUtil.setTopic(null, topicInfo);
	}

}
