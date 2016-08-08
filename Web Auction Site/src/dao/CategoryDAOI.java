package dao;

import entities.Category;

public interface CategoryDAOI {
	public Category find(String name);
	
	public boolean create(Category category);
}
