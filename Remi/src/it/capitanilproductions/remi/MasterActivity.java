package it.capitanilproductions.remi;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
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
	
	private SQLiteDatabase db=null;
	private SQLiteOpenHelper helper=null;
	private Cursor query;
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
//      ottengo liste e relativi meta-dati
        helper=new DBList(this);
        db=helper.getWritableDatabase();
        query=db.query(DBList.LIST_TABLE, null, null, null, null, null, null);
//      riempio la listView del layout dell'activity master
        ListAdapter adapter=new SimpleCursorAdapter(this, R.layout.master_row, query, columnsArray, textViewArray, 0);
        setListAdapter(adapter);
        
        lw=(ListView)findViewById(android.R.id.list);
        lw.setOnItemClickListener(this);
        lw.setOnItemLongClickListener(this);
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
			updateView(DBList.COLOUMN_NAME+" LIKE '"+newText+"%'");
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
		query.moveToPosition(position);
		intent.putExtra(CHOSENLIST, query.getString(1));
		intent.putExtra(CHOSENLISTABORDER, (query.getInt(4)==1) ? true : false);
		intent.putExtra(CHOSENLISTMTBOTTOM, (query.getInt(5)==1) ? true : false);
		startActivity(intent);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//		long click sul nome di una lista
		selectedPosition=position;
		query.moveToPosition(position);
		DialogFragment frag=new DeleteListDialog();
		Bundle args=new Bundle();
		args.putString("oldListName", ((TextView)view.findViewById(R.id.rowListName)).getText().toString());
		args.putBoolean("oldAbo", query.getInt(4)==1 ? true : false);
		args.putBoolean("oldMtb", query.getInt(5)==1 ? true : false); //caricare nel dialog i vecchi valori di move to bottom e alpha-order
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
		String name=((EditText)dialogView.findViewById(R.id.new_list_name)).getText().toString();
		name=name.replace("'", "''");
//		check per vedere se la lista esiste gia
		if(db.query(DBList.LIST_TABLE, columnNameArray, DBList.COLOUMN_NAME+"='"+name+"'", null, null, null, null).getCount()!=0){
			Toast.makeText(getApplicationContext(), R.string.double_list_toast, Toast.LENGTH_LONG).show();
			return;
		}
		final boolean moveToBottom=((CheckBox)dialogView.findViewById(R.id.new_list_mtb)).isChecked();
		final boolean abOrder=((CheckBox)dialogView.findViewById(R.id.new_list_abo)).isChecked();
		ContentValues values=new ContentValues();
		values.put(DBList.COLOUMN_NAME, name);
		values.put(DBList.COLOUMN_MOVETOBOTTOM, (moveToBottom==true) ? 1 : 0);
		values.put(DBList.COLOUMN_ABORDER, (abOrder==true) ? 1 : 0);
		values.put(DBList.COLOUMN_TOTAL, 0);
		values.put(DBList.COLOUMN_CHECKED, 0);
		
		db.insert(DBList.LIST_TABLE, null, values);
		updateView(null);
		
	}
	
	public void confirmModifyList(View dialogView) {
		String name=((EditText)dialogView.findViewById(R.id.new_list_name)).getText().toString();
		name=name.replace("'", "''");
		if(db.query(DBList.LIST_TABLE, columnNameArray, DBList.COLOUMN_NAME+"='"+name+"'", null, null, null, null).getCount()!=0){
			Toast.makeText(getApplicationContext(), R.string.double_list_toast, Toast.LENGTH_LONG).show();
			return;
		}
		final boolean moveToBottom=((CheckBox)dialogView.findViewById(R.id.new_list_mtb)).isChecked();
		final boolean abOrder=((CheckBox)dialogView.findViewById(R.id.new_list_abo)).isChecked();
		ContentValues values=new ContentValues();
		values.put(DBList.COLOUMN_NAME, name);
		values.put(DBList.COLOUMN_MOVETOBOTTOM, (moveToBottom==true) ? 1 : 0);
		values.put(DBList.COLOUMN_ABORDER, (abOrder==true) ? 1 : 0);
//		ottengo il nome dalla lista da cancellare dal cursor
		query.moveToPosition(selectedPosition);
		db.update(DBList.LIST_TABLE, values, DBList.COLOUMN_NAME+"='"+query.getString(1)+"'", null);
		updateView(null);
	}
	
	public void confirmDeleteList(String list){
//		click positivo nel dialog per l'eliminazione di una lista
//		ottenere il nome della lista da cancellare dal cursor rende il codice più robusto
//		nel caso in cui uno cambia il nome e poi chiede l'eliminazione
		query.moveToPosition(selectedPosition);
		db.delete(DBList.LIST_TABLE, DBList.COLOUMN_NAME+"='"+query.getString(1)+"'", null);
		updateView(null);
	}
	
	
//	metodi privati di varia utilità
	private void updateView(String selection){
        query=db.query(DBList.LIST_TABLE, null, selection, null, null, null, null);
        ListAdapter adapter=new SimpleCursorAdapter(this, R.layout.master_row, query, columnsArray, textViewArray, 0);
        setListAdapter(adapter);
	}



}
//TODO: devo fare il cursor adapter custom come per la detail activity(così risolvo anche il problema degli apostrofi
//TODO: Dopo va aggiunta la nuova activity che gestisca una singola lista di elementi.
//TODO: Sarebbe auspicabile anche fare in modo che l'ultima lista acceduta risulti la prima nella lista
//TODO: dovrei fare in modo che le liste siano uniche (i nomi) -> fatto
