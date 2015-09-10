package com.common.model;

/*用于分页*/
public class ReturnList {

	// 总数
	private int total;
	// 列表数据
	private Object list;

	public Object getList() {
		return list;
	}

	public void setList(Object list) {
		this.list = list;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}
