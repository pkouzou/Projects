package utils;

public class CustomQuery {
	private String field;
	private String searchTerm;
	
	public CustomQuery(String field, String searchTerm){
		this.field = field;
		this.searchTerm = searchTerm;
	}
	
	public String getField(){
		return(field);
	}
	
	public String getSearchTerm(){
		return(searchTerm);
	}
}