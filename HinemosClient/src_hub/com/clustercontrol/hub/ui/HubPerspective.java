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
package com.clustercontrol.hub.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;

import com.clustercontrol.ClusterControlPerspectiveBase;
import com.clustercontrol.hub.view.TransferView;
import com.clustercontrol.hub.view.LogScopeTreeView;

public class HubPerspective extends ClusterControlPerspectiveBase {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		super.createInitialLayout(layout);

		//エディタ領域のIDを取得
		String editorArea = layout.getEditorArea();

		IFolderLayout left = layout.createFolder( "left",IPageLayout.LEFT, 0.20f, editorArea );
		IFolderLayout right = layout.createFolder( "right",IPageLayout.RIGHT, 0.95f, editorArea );
		
		left.addView(LogScopeTreeView.ID);
		//right.addView(LogSummaryView.ID);
		//right.addView(LogSearchView.ID);
		right.addView(TransferView.ID);
	}
}
