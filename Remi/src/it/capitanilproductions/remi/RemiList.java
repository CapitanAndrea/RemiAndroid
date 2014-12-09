package it.capitanilproductions.remi;

public class RemiList {

	String name;
	int totalItems;
	int checkedItems;
	boolean abOrder;
	boolean mtBottom;
	
	public RemiList(String newListName, boolean isABO, boolean isMTB) {
		name=newListName;
		abOrder=isABO;
		mtBottom=isMTB;
		totalItems=0;
		checkedItems=0;
	}
	
	public RemiList(String newListName, int total, int checked, boolean isABO, boolean isMTB) {
		name=newListName;
		abOrder=isABO;
		mtBottom=isMTB;
		totalItems=total;
		checkedItems=checked;
	}
	
	@Override
	public String toString() {
		String concat="";

		concat=concat.concat("name: "+name)
			.concat("\ntotal: "+totalItems)
			.concat("\ncheck: "+checkedItems)
			.concat("\nabo: "+abOrder)
			.concat("\nmtb: "+mtBottom);
		return concat;
	}

}
