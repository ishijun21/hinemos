/*

Copyright (C) since 2006 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.repository.util;

import com.clustercontrol.repository.bean.FacilityConstant;
import com.clustercontrol.repository.model.FacilityInfo;
import com.clustercontrol.repository.model.NodeInfo;

public class FacilityValidator {

	/**
	 * ノードインスタンスからノード情報を生成する。<BR>
	 * ただし、ノード情報は一部の情報のみ。
	 * 
	 * @param node ノードインスタンス
	 * @return ノード情報
	 */
	public static NodeInfo nodeToArrayList(NodeInfo node) {
		NodeInfo nodeInfo = new NodeInfo();
		nodeInfo.setFacilityId(node.getFacilityId());
		nodeInfo.setFacilityName(node.getFacilityName());
		nodeInfo.setIpAddressVersion(node.getIpAddressVersion());
		nodeInfo.setIpAddressV4(node.getIpAddressV4());
		nodeInfo.setIpAddressV6(node.getIpAddressV6());
		nodeInfo.setPlatformFamily(node.getPlatformFamily());
		nodeInfo.setDescription(node.getDescription());
		nodeInfo.setOwnerRoleId(node.getOwnerRoleId());

		return nodeInfo;
	}

	/**
	 * ファシリティインスタンスからファシリティ情報を生成する。<BR>
	 * ただし、ファシリティ情報は以下の形式で格納されている。<BR>
	 * <PRE>
	 * {
	 *    {facilityId1, facilityName1, description1, displaySortOrder1},
	 *    {facilityId2, facilityName2, description2, displaySortOrder2},
	 *    ...
	 * }
	 * </PRE>
	 * 
	 * @param scope ファシリティインスタンス
	 * @return ファシリティ情報
	 */
	public static FacilityInfo facilityToArrayList(FacilityInfo facility) {
		FacilityInfo facilityInfo = new FacilityInfo();
		if (facility instanceof NodeInfo) {
			facilityInfo.setFacilityType(FacilityConstant.TYPE_NODE);
		} else {
			facilityInfo.setFacilityType(FacilityConstant.TYPE_SCOPE);
		}
		facilityInfo.setFacilityId(facility.getFacilityId());
		facilityInfo.setFacilityName(facility.getFacilityName());
		facilityInfo.setDescription(facility.getDescription());
		facilityInfo.setDisplaySortOrder(facility.getDisplaySortOrder());
		facilityInfo.setIconImage(facility.getIconImage());
		facilityInfo.setOwnerRoleId(facility.getOwnerRoleId());
		return facilityInfo;
	}

}
