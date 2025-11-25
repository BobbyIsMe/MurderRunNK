package com.joshuacc.mrnk.main;

public class MRItemFilters {

	private String keyword;
	private String price;
	private String type;
	private int page;
	
	public MRItemFilters(String keyword, String price, String type, int page)
	{
		this.keyword = keyword;
		this.price = price;
		this.type = type;
		this.page = page;
	}
	
	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}
	
	public void setPrice(String price)
	{
		this.price = price;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public void setPage(int page)
	{
		this.page = page;
	}
	
	public String getKeyword()
	{
		return keyword;
	}
	
	public String getPrice()
	{
		return price;
	}
	
	public String getType()
	{
		return type;
	}
	
	public int getPage()
	{
		return page;
	}
}
