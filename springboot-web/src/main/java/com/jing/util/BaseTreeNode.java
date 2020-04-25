package com.jing.util;

import java.util.ArrayList;
import java.util.List;

public class BaseTreeNode {
	/**
	 * 子Id
	 */
	private Long id;
	/**
	 * 父ID
	 */
	private Long pid;

	private List<BaseTreeNode> child;

	public BaseTreeNode() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public List<BaseTreeNode> getChild() {
		return this.child;
	}

	public void setChild(List<BaseTreeNode> child) {
		this.child = child;
	}

	public void addChild(BaseTreeNode baseTreeNode) {
		if (this.child == null) {
			this.setChild(new ArrayList<BaseTreeNode>());
		}

		this.getChild().add(baseTreeNode);
	}

	@Override
	public String toString() {
		return "BaseTreeNode [id=" + id + ", pid=" + pid + ", child=" + child + "]";
	}
}