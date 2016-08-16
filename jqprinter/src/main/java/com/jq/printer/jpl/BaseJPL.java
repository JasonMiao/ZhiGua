package com.jq.printer.jpl;

import com.jq.printer.Port;

public class BaseJPL 
{
	protected Port port;
	protected JPL_Param param;

	/*
	 * ¹¹Ôìº¯Êý
	 */
	public BaseJPL(JPL_Param param) {
		if (param.port == null)
			return;
		this.param = param;
		this.port = param.port;
	}
}
