package uk.ac.gla.jagora;

public class Stock {
	
	public final String name;

	public Stock(String identifier) {
		this.name = identifier;
	}
	
	@Override
	public String toString (){
		return name;
	}

}
