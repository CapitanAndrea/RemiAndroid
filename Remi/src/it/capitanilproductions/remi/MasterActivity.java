package it.capitanilproductions.remi;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

public class MasterActivity extends ListActivity implements OnItemClickListener, OnItemLongClickListener, OnQueryTextListener, OnActionExpandListener {

	private static final String TAG="REMI-MASTER";
	private static final int TOP=0;
	
	static final int CODE=43;
	
	List<RemiList> lists;
	List<RemiList> queriedLists=new ArrayList<RemiList>();
	public static final String LISTNAME="ListName";
	public static final String TOTALITEMS="TotalItems";
	public static final String CHECKEDITEMS="Checkeditems";
	public static final String ABOOPTION="ABOEnabled";
	public static final String MTBOPTION="MTBEnabled";
	
	public static final String CHOSENLIST="it.capitanilproductions.remi.LIST";
	public static final String CHOSENLISTABORDER="it.capitanilproductions.remi.ABORDER";
	public static final String CHOSENLISTMTBOTTOM="it.capitanilproductions.remi.MTBOTTOM";
	
	public static final String RETURNEDCHECKEDITEMS="it.capitanilproductions.remiReturnCheck";
	public static final String RETURNEDTOTALITEMS="it.capitanilproductions.remiReturnTotal";
	
	private ListView lw=null;
	
	private int selectedPosition;
	private int dialog;
	
//	override metodi per creazione, pausa, restore etc etc activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        
        lists=new ArrayList<RemiList>();
        loadData();
        
        /*if(lists!=null){
        	ListAdapter adapter=new MasterArrayAdapter(this, R.layout.master_row, lists);
        	setListAdapter(adapter);
        }*/
        
        lw=(ListView)findViewById(android.R.id.list);
        lw.setOnItemClickListener(this);
        lw.setOnItemLongClickListener(this);
    }
    
    private void loadData(){
//    	clear the list if it's not empty
    	if(lists==null) lists=new ArrayList<RemiList>();
    	else if(!lists.isEmpty()) lists.clear();
    	
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
        updateView(null);
    	return;
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
        storeData();
    }
    
    private void storeData(){
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
    	case R.id.export_data:{
    		if(!isExternalStorageWritable()){
    			Toast.makeText(this, "External storage is not accessible", Toast.LENGTH_SHORT).show();
    			return true;
    		}
    		storeData();
    		File[] extFiles=getExternalFilesDir(null).listFiles();
    		for(File external:extFiles) external.delete();
    		File[] lists=getFilesDir().listFiles();
    		File[] dirs={getFilesDir(), getExternalFilesDir(null)};
    		lists=concat(dirs, lists);
    		new ExportData(this).execute(lists);
    		return true;
    	}
    	case R.id.import_data:{
//    		if external storage is not accessible don't do anything!
    		if(!isExternalStorageReadable()){
    			Toast.makeText(this, "External storage is not accessible", Toast.LENGTH_SHORT).show();
    			return true;
    		}
    		File[] intFiles=getFilesDir().listFiles();
    		for(File internal:intFiles) internal.delete();
    		File[] lists=getExternalFilesDir(null).listFiles();
    		File[] dirs={getExternalFilesDir(null), getFilesDir()};
    		lists=concat(dirs, lists);
    		new ImportData(this).execute(lists);
    		return true;
    	}
    	default:{
    		Toast.makeText(this, "Bottone sconosciuto pigiato", Toast.LENGTH_LONG).show();
    		return true;
    	}
    	}
    }
    
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
    
	/* Checks if external storage is available to at least read */
	private boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
    
    private File[] concat(File[] a, File[] b) {
    	   int aLen = a.length;
    	   int bLen = b.length;
    	   File[] c= new File[aLen+bLen];
    	   System.arraycopy(a, 0, c, 0, aLen);
    	   System.arraycopy(b, 0, c, aLen, bLen);
    	   return c;
    	}
    
    public void postImport(){
    	Toast.makeText(this, "Data import succesfull!!", Toast.LENGTH_SHORT).show();
    	loadData();
    }

    public void postExport(){
    	Toast.makeText(this, "Data export succesfull!!", Toast.LENGTH_SHORT).show();
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
		selectedPosition=position;
		RemiList selectedList=queriedLists.get(selectedPosition);
		lists.remove(selectedPosition);
		lists.add(TOP, selectedList);
		intent.putExtra(CHOSENLIST, selectedList.name);
		intent.putExtra(CHOSENLISTABORDER, selectedList.abOrder);
		intent.putExtra(CHOSENLISTMTBOTTOM, selectedList.mtBottom);
		startActivityForResult(intent, CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_CANCELED) return;
		RemiList selectedList=queriedLists.get(selectedPosition);
		int retval=data.getIntExtra(RETURNEDCHECKEDITEMS, -1);
		if(retval!=-1) selectedList.checkedItems=retval;
		retval=data.getIntExtra(RETURNEDTOTALITEMS, -1);
		if(retval!=-1) selectedList.totalItems=retval;
//		Log.d(TAG, "List "+selectedList.name+"\nChecked "+selectedList.checkedItems+"\nTotal "+selectedList.totalItems);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//		long click on a list's name
		selectedPosition=position;
		DialogFragment frag=new DeleteListDialog();
		Bundle args=new Bundle();
//		obtaining the list and putting in args the list's current fields
		RemiList selectedList=queriedLists.get(position);
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
//		positive click in the "create new list" dialog
		String newListName=((EditText)dialogView.findViewById(R.id.new_list_name)).getText().toString();

//		check for an existing list with the requested name
		for(RemiList checkList:lists){
			if(checkList.name==newListName){
				Toast.makeText(getApplicationContext(), R.string.double_list_toast, Toast.LENGTH_LONG).show();
				return;
			}
		}
		final boolean moveToBottom=((CheckBox)dialogView.findViewById(R.id.new_list_mtb)).isChecked();
		final boolean abOrder=((CheckBox)dialogView.findViewById(R.id.new_list_abo)).isChecked();

		try {
			new File(getFilesDir(), newListName).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lists.add(TOP, new RemiList(newListName, abOrder, moveToBottom));
		updateView(null);
		
		Intent intent=new Intent(this, DetailActivity.class);
		intent.putExtra(CHOSENLIST, newListName);
		intent.putExtra(CHOSENLISTABORDER, abOrder);
		intent.putExtra(CHOSENLISTMTBOTTOM, moveToBottom);
		startActivityForResult(intent, CODE);
		
	}
	
	public void confirmModifyList(View dialogView) {
		String newListName=((EditText)dialogView.findViewById(R.id.new_list_name)).getText().toString();
		RemiList selectedList=queriedLists.get(selectedPosition);
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
//		update the file name containing items for the list
		File items=new File(getFilesDir(), selectedList.name);
		items.renameTo(new File(items.getParentFile(), newListName));
		final boolean moveToBottom=((CheckBox)dialogView.findViewById(R.id.new_list_mtb)).isChecked();
		final boolean abOrder=((CheckBox)dialogView.findViewById(R.id.new_list_abo)).isChecked();
//		ottengo il nome dalla lista da cancellare dal cursor
		selectedList.name=newListName;
		selectedList.abOrder=abOrder;
		selectedList.mtBottom=moveToBottom;
		updateView(null);
	}
	
	public void confirmDeleteList(String list){
//		click positivo nel dialog per l'eliminazione di una lista
//		ottenere il nome della lista da cancellare dalla struttura dati che contiene le liste
//		rende il codice più robusto nel caso in cui uno cambia il nome e poi chiede l'eliminazione
		if(!queriedLists.isEmpty()){
			selectedPosition=lists.indexOf(queriedLists.get(selectedPosition));
		}
		lists.remove(selectedPosition);
		updateView(null);
	}
	
	
//	metodi privati di varia utilità
	private void updateView(String selection){
//		if there is a text in the query
		queriedLists.clear();
		if(selection!=null){
			Log.d(TAG, selection);
			for(RemiList currList : lists){
//				Log.d(TAG, "Checking list "+currList.name);
//				add in queriedLists each lists whose name contains the query text
				if(currList.name.contains(selection)){
					queriedLists.add(currList);
					Log.d(TAG, currList.name);
				}
			}
		} else {
			queriedLists.addAll(lists);
		}
        ListAdapter adapter=new MasterArrayAdapter(this, R.layout.master_row, queriedLists);
        setListAdapter(adapter);
	}
}