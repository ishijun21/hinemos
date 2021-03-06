/*

Copyright (C) 2016 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */
package com.clustercontrol.hub.session;

import com.clustercontrol.hub.model.CollectStringData;
import com.clustercontrol.hub.model.CollectStringKeyInfo;

/**
 * 文字列情報を纏めて、Transfer へ渡す際に使用する
 * 
 *
 */
public class TransferStringData {
	public final CollectStringKeyInfo key;
	public final CollectStringData data;
	
	public TransferStringData(CollectStringKeyInfo key, CollectStringData data) {
		this.key = key;
		this.data = data;
	}
}
