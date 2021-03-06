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

package com.clustercontrol.jobmanagement.action;

import java.util.ArrayList;

import org.eclipse.swt.SWT;

import com.clustercontrol.bean.TableColumnInfo;
import com.clustercontrol.util.Messages;

/**
 * 環境変数のテーブル定義を取得するクライアント側アクションクラス<BR>
 *
 * @version 6.0.0
 * @since 6.0.0
 */
public class GetEnvVariableTableDefine {

	/** 名前 */
	public static final int ENV_VARIABLE_ID = 0;
	/** 値 */
	public static final int VALUE = 1;
	/** 説明 */
	public static final int DESCRIPTION = 2;

	/** 初期表示時ソートカラム */
	public static final int SORT_COLUMN_INDEX = ENV_VARIABLE_ID;
	/** 初期表示時ソートオーダー (昇順=1, 降順=-1) */
	public static final int SORT_ORDER = 1;

	/**
	 * 環境変数のテーブル定義を作成する
	 *
	 * @return テーブル定義情報（{@link com.clustercontrol.bean.TableColumnInfo}のリスト）
	 */
	public static ArrayList<TableColumnInfo> get() {
		//テーブル定義配列
		ArrayList<TableColumnInfo> tableDefine = new ArrayList<TableColumnInfo>();

		tableDefine.add(ENV_VARIABLE_ID,
				new TableColumnInfo(Messages.getString("name"), TableColumnInfo.NONE, 120, SWT.LEFT));
		tableDefine.add(VALUE,
				new TableColumnInfo(Messages.getString("value"), TableColumnInfo.NONE, 160, SWT.LEFT));
		tableDefine.add(DESCRIPTION,
				new TableColumnInfo(Messages.getString("description"), TableColumnInfo.NONE, 150, SWT.LEFT));

		return tableDefine;
	}
}
