package org.unimelb.itime.test.david;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.unimelb.itime.test.R;
import org.unimelb.itime.vendor.contact.SortAdapter;
import org.unimelb.itime.vendor.contact.helper.CharacterParser;
import org.unimelb.itime.vendor.contact.helper.ClearEditText;
import org.unimelb.itime.vendor.contact.helper.LoadImgHelper;
import org.unimelb.itime.vendor.contact.helper.PinyinComparator;
import org.unimelb.itime.vendor.contact.widgets.Contact;
import org.unimelb.itime.vendor.contact.widgets.SideBar;
import org.unimelb.itime.vendor.contact.widgets.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContactsPageFragment extends Fragment {
	public Map<Contact, ImageView> contacts_list = new HashMap<Contact, ImageView>();

	private static final String TAG = "MyAPP";
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	private Map<String, Contact> contacts;

	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	private PinyinComparator pinyinComparator;
	private View root;
	private Context context;

	private ContactsPageFragment self = this;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		context = getActivity().getApplicationContext();
		root = inflater.inflate(R.layout.itime_contact_fragment, container, false);
		// Inflate the layout for this fragment
		initView();
		initData();

		return root;
	}

	public Map<Contact, ImageView> getAllSelectedContacts(){
        return this.contacts_list;
    }

	private void initView() {

		sideBar = (SideBar) root.findViewById(R.id.sidrbar);
		dialog = (TextView) root.findViewById(R.id.dialog);

		sortListView = (ListView) root.findViewById(R.id.sortlist);
	}

	private void initData() {
		//set body
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar.setTextView(dialog);

		sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

			@SuppressLint("NewApi")
			@Override
			public void onTouchingLetterChanged(String s) {
				int nearestPreChar = adapter.findNearestPreMatch(s.toUpperCase().charAt(0));
				int position = adapter.getPositionForSection(nearestPreChar);
				if (position != -1) {
					sortListView.setSelection(position);
				}
			}
		});

		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});

		new ContactsAsyncTask().execute(0);

	}

	private class ContactsAsyncTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... arg0) {
			int result = -1;
            //load contacts info
			contacts = simulateContacts();
			result = 1;
			return result;
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == 1) {
				final List<String> constact = new ArrayList<String>();
				for (Iterator<String> keys = contacts.keySet().iterator(); keys
						.hasNext();) {
					String key = keys.next();
					constact.add(key);
				}
				String[] names = new String[] {};
				names = constact.toArray(names);
				SourceDateList = filledData(names);

				Collections.sort(SourceDateList, pinyinComparator);
				adapter = new SortAdapter(getActivity().getApplicationContext(), SourceDateList, contacts);
				sortListView.setAdapter(adapter);

				mClearEditText = (ClearEditText) root
						.findViewById(R.id.filter_edit);
				mClearEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						mClearEditText.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
						
					}
				});
				mClearEditText.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						filterData(s.toString().toLowerCase());
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});

				//connect checked list
				adapter.setCircleCheckOnClickListener(new SortAdapter.CircleCheckOnClickListener() {
					LinearLayout ll_checkedList = (LinearLayout) root.findViewById(R.id.checked_contacts_list);
					int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
					int margin = width/40;
					@Override
					public void synCheckedContactsList(Contact contact, boolean add) {
						if (add){
							ImageView img_v = new ImageView(context);
                            img_v.setOnClickListener(new ContactViewTouchListener());
                            img_v.setTag(contact);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width/8,width/8);
							params.setMargins(margin, margin/2, 0, margin/2);
							img_v.setLayoutParams(params);
							contacts_list.put(contact, img_v);
							LoadImgHelper.getInstance().bindUrlWithImageView(
									context, contact, img_v);
							ll_checkedList.addView(img_v);
							ll_checkedList.invalidate();
							Log.i(TAG, "add: ");
						}else {
							ll_checkedList.removeView(contacts_list.get(contact));
							contacts_list.remove(contact);
							ll_checkedList.invalidate();
							Log.i(TAG, "remove: ");
						}
					}

					@Override
					public Map getMapInContactsList() {
						return contacts_list;
					}
				});
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

	}

	private List<SortModel> filledData(String[] date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.length; i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName().toLowerCase();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	public Map simulateContacts(){
		Map<String, Contact> map = new HashMap<>();
//		map.put("Angelababy",new Contact("http://i1.wp.com/pmcdeadline2.files.wordpress.com/2016/06/angelababy.jpg?crop=0px%2C107px%2C1980px%2C1327px&resize=446%2C299&ssl=1","Angelababy"));
		map.put("赵普",new Contact(null,"赵普"));
		map.put("Crron",new Contact(null,"Crron"));
		map.put("Bob",new Contact(null,"Bob"));
//		map.put("Alice",new Contact("http://education.news.cn/2015-05/04/127751980_14303593148421n.jpg","Crron"));
		map.put("赵普 3",new Contact(null,"赵普"));
		map.put("周二珂",new Contact("http://esczx.baixing.com/uploadfile/2016/0427/20160427112336847.jpg","周二珂"));
//		map.put("David Liu",new Contact(null,"David Liu"));
		map.put("Kangaroo",new Contact("http://static.ettoday.net/images/1114/d1114210.jpg","David Liu"));
		map.put("Crron 4",new Contact(null,"Crron"));
		map.put("H哥",new Contact(null,"赵普"));
//		map.put("刘诗诗",new Contact("http://img.zybus.com/uploads/allimg/131213/1-131213111353.jpg","刘诗诗"));
		map.put("U哥",new Contact(null,"赵普"));
		map.put("R哥",new Contact(null,"赵普"));
		map.put("E哥",new Contact(null,"赵普"));
		map.put("G哥",new Contact(null,"赵普"));
		map.put("F哥",new Contact(null,"Crron"));
		return map;
	}

    class ContactViewTouchListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            ViewGroup parent = (ViewGroup) view.getParent();

            if (parent != null){
                parent.removeView(view);
                contacts_list.remove(view.getTag());
                adapter.notifyDataSetChanged();
            }
        }
    }

}
