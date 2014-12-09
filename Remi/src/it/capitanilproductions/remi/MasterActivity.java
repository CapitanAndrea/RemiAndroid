package it.capitanilproductions.remi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MasterActivity extends ListActivity implements OnItemClickListener, OnItemLongClickListener, OnQueryTextListener, OnActionExpandListener {

	private static final String TAG="REMI";
	private static final int TOP=0;
	
	private SQLiteDatabase db=null;
	private SQLiteOpenHelper helper=null;
	private Cursor query;
	
	List<RemiList> lists;
	List<RemiList> queriedLists=new ArrayList<RemiList>();
	public static final String LISTNAME="ListName";
	public static final String TOTALITEMS="TotalItems";
	public static final String CHECKEDITEMS="Checkeditems";
	public static final String ABOOPTION="ABOEnabled";
	public static final String MTBOPTION="MTBEnabled";
	
	private int dialog;
	
	public static final String CHOSENLIST="it.capitanilproductions.remi.LIST";
	public static final String CHOSENLISTABORDER="it.capitanilproductions.remi.ABORDER";
	public static final String CHOSENLISTMTBOTTOM="it.capitanilproductions.remi.MTBOTTOM";
	
	private String columnNameArray []={
			DBList.COLOUMN_NAME
	};
	private String columnsArray []={
		DBList.COLOUMN_NAME,
		DBList.COLOUMN_TOTAL,
		DBList.COLOUMN_CHECKED,
		DBList.COLOUMN_ID
	};
	private int textViewArray []={
			R.id.rowListName,
			R.id.itemsChecked,
			R.id.totalItems
	};
	private ListView lw=null;
	
	private int selectedPosition;
	
//	override metodi per creazione, pausa, restore etc etc activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
/*      BETTER GO JSON hue-hue-hue--
//      ottengo liste e relativi meta-dati
        helper=new DBList(this);
        db=helper.getWritableDatabase();
        query=db.query(DBList.LIST_TABLE, null, null, null, null, null, null);
//      riempio la listView del layout dell'activity master
        ListAdapter adapter=new MasterCursorAdapter(this, R.layout.master_row, query, 0);
*/
        
        lists=new ArrayList<RemiList>();
        JsonReader reader=null;
        try{
	        InputStreamReader buf = new InputStreamReader(new FileInputStream(this.getFilesDir().getPath().toString()+"/lists"));
	        reader=new JsonReader(new BufferedReader(buf));
//	        Log.d(TAG, "Begin loading data...");
			reader.beginArray();
	        while(reader.hasNext()){
	        	RemiList nextList=readNextList(reader);
	        	if(nextList!=null) lists.add(nextList);
//	        	Log.d(TAG, "Just read a list: "+nextList);
	        }
	        reader.endArray();
	        reader.close();
//	        Log.d(TAG, "All lists succesfully read");
        } catch(Exception e){
        	try {
//        		Log.d(TAG, "Loading failed...", e);
				if(reader!=null) reader.close();
			} catch (IOException e1) {
				Toast.makeText(this, "List loading failed, report to developer!!", Toast.LENGTH_SHORT).show();
			}
        }
        
        /*if(lists!=null){
        	ListAdapter adapter=new MasterArrayAdapter(this, R.layout.master_row, lists);
        	setListAdapter(adapter);
        }*/
        
        lw=(ListView)findViewById(android.R.id.list);
        lw.setOnItemClickListener(this);
        lw.setOnItemLongClickListener(this);
    }
    
    /**
     * Read the next JSON object from reader and returns the corresponding RemiList object
     * @param reader
     * @return a RemiList object representing a list, null if an exception was thrown while reading data
     */
    private RemiList readNextList(JsonReader reader) {
		String name=null;
		boolean listMTB=false;
		boolean listABO=false;
		int checked=0;
		int total=0;
		
		try{
			reader.beginObject();
			while(reader.hasNext()){
				String key=reader.nextName();
				switch(key){
				case LISTNAME:{
					name=reader.nextString();
					break;
				}
				case TOTALITEMS:{
					total=reader.nextInt();
					break;
				}
				case CHECKEDITEMS:{
					checked=reader.nextInt();
					break;
				}
				case ABOOPTION:{
					listABO=reader.nextBoolean();
					break;
				}
				case MTBOPTION:{
					listMTB=reader.nextBoolean();
					break;
				}
				default: break;
				}
			}
			reader.endObject();
		} catch (Exception e){
			return null;
		}
		return new RemiList(name, total, checked, listABO, listMTB);
	}

    @Override
    protected void onStart() {
    	super.onStart();
    	updateView(null);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
//    	Log.d(TAG, "onPause");
        File metadata=new File(this.getFilesDir(), "lists");
        try {
			metadata.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        JsonWriter writer=null;
        try{
	        BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(this.getFilesDir().getPath().toString()+"/lists"));
	        writer=new JsonWriter(new OutputStreamWriter(buf, "UTF-8"));
	        
	        writer.beginArray();
//	        Log.d(TAG, "Start writing data... may take some time");
	        for(RemiList currentList:lists){
//	        	Log.d(TAG, "writing: "+currentList.toString());
	        	writer.beginObject();
	        	writer.name(LISTNAME).value(currentList.name);
	        	writer.name(TOTALITEMS).value(currentList.totalItems);
	        	writer.name(CHECKEDITEMS).value(currentList.checkedItems);
	        	writer.name(ABOOPTION).value(currentList.abOrder);
	        	writer.name(MTBOPTION).value(currentList.mtBottom);
	        	writer.endObject();
//	        	Log.d(TAG, "Object succesfully wrote");
	        }
	        writer.endArray();
	        writer.flush(); //needed to flush because the jsonArray closed before any byte was written
//	        Log.d(TAG, "All data succesfully stored!!");
        }catch (Exception e){
//        	Log.d(TAG, "Issues saving data", e);
        	try {
				if(writer!=null) writer.close();
			} catch (IOException e1) {
			}
        }
    }
    
//    override metodi per creazione e gestione menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater=getMenuInflater();
    	inflater.inflate(R.menu.master_activity_actions, menu);
    	
    	MenuItem searchMenuItem=menu.findItem(R.id.action_searchlist);
    	searchMenuItem.setOnActionExpandListener(this);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
    	
    	return true; 
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	case R.id.action_searchlist:{
    		return true;
    	}
    	case R.id.action_add_list:{
//    		Toast.makeText(this, "Nuova lista creata!!", Toast.LENGTH_SHORT).show();
    		dialog=R.string.list_creation;
    		showDialog();
    		return true;
    	}
    	default:{
    		Toast.makeText(this, "Bottone sconosciuto pigiato", Toast.LENGTH_LONG).show();
    		return true;
    	}
    	}
    }
    
//  override dei metodi delle interfacce dei listener per la serachview pheega alla What'sApp
	@Override
	public boolean onQueryTextSubmit(String query) {
		return true; //tanto la serachView non fa niente di default
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		//per ogni nuova ricerca ricarico le liste che matchano
		if(!newText.isEmpty()){
			updateView(newText);
		} else{
			updateView(null);
		}
		return true;
	}

	@Override
	public boolean onMenuItemActionExpand(MenuItem item) {
		return true;
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem item) {
//		quando la serachview collassa faccio ricaricare tutte le liste
		if(item.getItemId()==R.id.action_searchlist){
			updateView(null);
		}
		return true;
	}

//    override metodi delle interfacce per gestire click etc etc (nella view)
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Intent intent=new Intent(this, DetailActivity.class);
		/*query.moveToPosition(position);
		intent.putExtra(CHOSENLIST, query.getString(query.getColumnIndex(DBList.COLOUMN_NAME)));
		intent.putExtra(CHOSENLISTABORDER, (query.getInt(4)==1) ? true : false);
		intent.putExtra(CHOSENLISTMTBOTTOM, (query.getInt(5)==1) ? true : false);*/
		RemiList selectedList=lists.get(position);
		lists.remove(position);
		lists.add(TOP, selectedList);
		intent.putExtra(CHOSENLIST, selectedList.name);
		intent.putExtra(CHOSENLISTABORDER, selectedList.abOrder);
		intent.putExtra(CHOSENLISTMTBOTTOM, selectedList.mtBottom);
		startActivity(intent);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//		long click sul nome di una lista
		selectedPosition=position;
//		query.moveToPosition(position);
		DialogFragment frag=new DeleteListDialog();
		Bundle args=new Bundle();
		/*args.putString("oldListName", ((TextView)view.findViewById(R.id.rowListName)).getText().toString());
		args.putBoolean("oldAbo", query.getInt(4)==1 ? true : false);
		args.putBoolean("oldMtb", query.getInt(5)==1 ? true : false); //caricare nel dialog i vecchi valori di move to bottom e alpha-order*/
		RemiList selectedList=lists.get(position);
		args.putString("oldListName", selectedList.name);
		args.putBoolean("oldAbo", selectedList.abOrder);
		args.putBoolean("oldMtb", selectedList.mtBottom);
		frag.setArguments(args);
		frag.show(getFragmentManager(), "delete_list");
		return true;
	}

//	metodi per il dialog
	public void showDialog(){
    	DialogFragment newFragment=MasterDialog.newInstance(dialog);
        newFragment.show(getFragmentManager(), "MasterDialog");
	}

	public void confirmCreateList(View dialogView) {
//		click positivo nel dialog per la creazione di una nuova lista
		String newListName=((EditText)dialogView.findViewById(R.id.new_list_name)).getText().toString();
		/*name=name.replace("'", "''");
//		check per vedere se la lista esiste gia
		if(db.query(DBList.LIST_TABLE, columnNameArray, DBList.COLOUMN_NAME+"='"+name+"'", null, null, null, null).getCount()!=0){

		}*/
		for(RemiList checkList:lists){
			if(checkList.name==newListName){
				Toast.makeText(getApplicationContext(), R.string.double_list_toast, Toast.LENGTH_LONG).show();
				return;
			}
		}
		final boolean moveToBottom=((CheckBox)dialogView.findViewById(R.id.new_list_mtb)).isChecked();
		final boolean abOrder=((CheckBox)dialogView.findViewById(R.id.new_list_abo)).isChecked();
		/*ContentValues values=new ContentValues();
		values.put(DBList.COLOUMN_NAME, name);
		values.put(DBList.COLOUMN_MOVETOBOTTOM, (moveToBottom==true) ? 1 : 0);
		values.put(DBList.COLOUMN_ABORDER, (abOrder==true) ? 1 : 0);
		values.put(DBList.COLOUMN_TOTAL, 0);
		values.put(DBList.COLOUMN_CHECKED, 0);
		db.insert(DBList.LIST_TABLE, null, values);*/
		lists.add(TOP, new RemiList(newListName, abOrder, moveToBottom));
		updateView(null);
		
	}
	
	public void confirmModifyList(View dialogView) {
		String newListName=((EditText)dialogView.findViewById(R.id.new_list_name)).getText().toString();
		/*name=name.replace("'", "''");
		query.moveToPosition(selectedPosition);
		if(!query.getString(query.getColumnIndex(DBList.COLOUMN_NAME)).equals(name) &&
			db.query(DBList.LIST_TABLE, columnNameArray, DBList.COLOUMN_NAME+"='"+name+"'", null, null, null, null).getCount()!=0){
			Toast.makeText(getApplicationContext(), R.string.double_list_toast, Toast.LENGTH_LONG).show();
			return;
		}*/
		RemiList selectedList=lists.get(selectedPosition);
		/*new list name is not the same as the old*/
		if(selectedList.name!=newListName){
			/*search for a list with the same name*/
			for(RemiList checklist:lists){
				if(checklist.name==newListName){
					Toast.makeText(getApplicationContext(), R.string.double_list_toast, Toast.LENGTH_LONG).show();
					return;
				}
			}
		}
		/*new name is unique, can now modify the list*/
		final boolean moveToBottom=((CheckBox)dialogView.findViewById(R.id.new_list_mtb)).isChecked();
		final boolean abOrder=((CheckBox)dialogView.findViewById(R.id.new_list_abo)).isChecked();
		/*ContentValues values=new ContentValues();
		values.put(DBList.COLOUMN_NAME, name);
		values.put(DBList.COLOUMN_MOVETOBOTTOM, (moveToBottom==true) ? 1 : 0);
		values.put(DBList.COLOUMN_ABORDER, (abOrder==true) ? 1 : 0);
//		ottengo il nome dalla lista da cancellare dal cursor
		db.update(DBList.LIST_TABLE, values, DBList.COLOUMN_NAME+"='"+query.getString(1)+"'", null);*/
		selectedList.name=newListName;
		selectedList.abOrder=abOrder;
		selectedList.mtBottom=moveToBottom;
		updateView(null);
	}
	
	public void confirmDeleteList(String list){
//		click positivo nel dialog per l'eliminazione di una lista
//		ottenere il nome della lista da cancellare dal cursor rende il codice più robusto
//		nel caso in cui uno cambia il nome e poi chiede l'eliminazione
		/*query.moveToPosition(selectedPosition);
		db.delete(DBList.LIST_TABLE, DBList.COLOUMN_NAME+"='"+query.getString(1)+"'", null);*/
		lists.remove(selectedPosition);
		updateView(null);
	}
	
	
//	metodi privati di varia utilità
	private void updateView(String selection){
//        query=db.query(DBList.LIST_TABLE, null, selection, null, null, null, null);
		if(selection!=null){
			queriedLists.clear();
			for(RemiList currList : lists){
				if(currList.name.contains(selection)){
					queriedLists.add(currList);
					Log.d(TAG, currList.name);
				}
			}
			
	        ListAdapter adapter=new MasterArrayAdapter(this, R.layout.master_row, queriedLists);
	        setListAdapter(adapter);
	        return;
		}
		
        ListAdapter adapter=new MasterArrayAdapter(this, R.layout.master_row, lists);
        setListAdapter(adapter);
	}



}
