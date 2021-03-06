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

package com.clustercontrol.http.factory;

import com.clustercontrol.commons.util.HinemosEntityManager;
import com.clustercontrol.commons.util.JpaTransactionManager;
import com.clustercontrol.fault.InvalidRole;
import com.clustercontrol.fault.MonitorNotFound;
import com.clustercontrol.http.util.ControlHttpInfo;
import com.clustercontrol.monitor.run.factory.ModifyMonitor;
import com.clustercontrol.monitor.run.factory.ModifyMonitorStringValueType;
import com.clustercontrol.plugin.impl.SchedulerPlugin.TriggerType;

/**
 * HTTP監視 文字列監視をマネージャで変更するクラス<BR>
 *
 * @version 2.1.0
 * @since 2.1.0
 * 
 */
public class ModifyMonitorHttpString extends ModifyMonitorStringValueType{

	/* (non-Javadoc)
	 * @see com.clustercontrol.monitor.run.factory.ModifyMonitor#modifyCheckInfo()
	 */
	@Override
	protected boolean modifyCheckInfo() throws MonitorNotFound, InvalidRole {

		// HTTP監視情報を変更
		return new ControlHttpInfo().modify(m_monitorInfo.getMonitorId(), m_monitorInfo.getHttpCheckInfo());
	}

	/**
	 * スケジュール実行の遅延時間を返します。
	 */
	@Override
	protected int getDelayTime() {
		return  ModifyMonitor.getDelayTimeBasic(m_monitorInfo);
	}

	/**
	 * スケジュール実行種別を返します。
	 */
	@Override
	protected TriggerType getTriggerType() {
		return TriggerType.SIMPLE;
	}

	@Override
	protected boolean addCheckInfo() throws MonitorNotFound, InvalidRole {
		// HTTP監視情報を追加
		HinemosEntityManager em = new JpaTransactionManager().getEntityManager();
		em.persist(m_monitorInfo.getHttpCheckInfo());
		return true;
	}
}
