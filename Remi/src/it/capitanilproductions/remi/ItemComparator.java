package it.capitanilproductions.remi;

import java.util.Comparator;

public class ItemComparator implements Comparator<RemiItem> {

	boolean mtb;
	boolean abo;
	
	public ItemComparator(boolean listMTBottom, boolean listABOrder) {
		mtb=listMTBottom;
		abo=listABOrder;
	}

	@Override
	public int compare(RemiItem lhs, RemiItem rhs) {
		if(mtb){
//			move to bottom enabled
			if(abo){
//				alphabetical order enabled
				if(lhs.check==rhs.check) return lhs.name.compareTo(rhs.name);
				if(lhs.check) return 1;
				return -1;
			}else{
//				alphabetical order disabled
				if(lhs.check==rhs.check) return 0;
				if(lhs.check) return 1;
				return -1;
			}
		} else{
//			move to bottom disabled
			if(abo){
//				alphabetical order enabled
				return lhs.name.compareTo(rhs.name);
			}else{
//				alphabetical order disabled
				return 0;
			}
		}
	}

}
