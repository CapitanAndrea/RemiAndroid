package it.capitanilproductions.remi;

public class RemiItem implements Comparable<RemiItem>{

	public static final int HIGH_PRIOROTY=0;
	public static final int MEDIUM_PRIOROTY=1;
	public static final int LOW_PRIOROTY=2;
	
	String name;
	boolean check;
	int priority;
	
	public RemiItem(String newItemName){
		name=newItemName;
//		by default is set priority as medium
		priority=MEDIUM_PRIOROTY;
	}
	
	public RemiItem(String newItemName, int itemPriority){
		name=newItemName;
		priority=itemPriority;
	}
	
	public RemiItem(String itemName, boolean itemCheck, int itemPriority){
		name=itemName;
		check=itemCheck;
		priority=itemPriority;
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
