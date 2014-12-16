package it.capitanilproductions.remi;

public class RemiItem implements Comparable<RemiItem>{

	String name;
	boolean check;
	
	public RemiItem(String newItemName){
		name=newItemName;
	}
	
	public RemiItem(String itemName, boolean itemCheck){
		name=itemName;
		check=itemCheck;
	}

	@Override
	public int compareTo(RemiItem another) {
		return name.compareToIgnoreCase(another.name);
	}
	
	@Override
	public String toString() {
		String concat="";
		concat=concat.concat("name: "+name)
				.concat("\ncheck: "+check);
		return concat;
	}
}
