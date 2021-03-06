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

package com.clustercontrol.repository.util;

import com.clustercontrol.commons.util.JpaTransactionCallback;
import com.clustercontrol.repository.factory.NodeProperty;

public class NodeCacheRemoveCallback implements JpaTransactionCallback {
	
	public final String facilityId;
	
	public NodeCacheRemoveCallback(String facilityId) {
		this.facilityId = facilityId;
	}
	
	@Override
	public void preBegin() { }

	@Override
	public void postBegin() { }

	@Override
	public void preFlush() { }

	@Override
	public void postFlush() { }

	@Override
	public void preCommit() { }

	@Override
	public void postCommit() {
		NodeProperty.removeNode(facilityId);
	}

	@Override
	public void preRollback() { }

	@Override
	public void postRollback() { }

	@Override
	public void preClose() { }

	@Override
	public void postClose() {}
	
	@Override
	public int hashCode() {
		int h = 1;
		h = h * 31 + (facilityId == null ? 0 : facilityId.hashCode());
		return h;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof NodeCacheRemoveCallback) {
			NodeCacheRemoveCallback cast = (NodeCacheRemoveCallback)obj;
			if (facilityId != null && facilityId.equals(cast.facilityId)) {
				return true;
			}
		}
		return false;
	}
	
}
