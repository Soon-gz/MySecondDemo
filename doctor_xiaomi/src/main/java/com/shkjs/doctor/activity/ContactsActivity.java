package com.shkjs.doctor.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;

import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.bean.Contact;
import com.shkjs.doctor.util.ContactAdapter;
import com.shkjs.doctor.util.HanziToPinyin;
import com.raspberry.library.util.ScreenUtils;
import com.shkjs.doctor.util.SideBar;
import com.raspberry.library.util.SystemStatusManager;

import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.kymjs.kjframe.KJActivity;
import org.kymjs.kjframe.ui.BindView;

import java.io.InputStream;
import java.util.ArrayList;

public class ContactsActivity extends KJActivity implements SideBar
        .OnTouchingLetterChangedListener, TextWatcher {
    @BindView(id = R.id.school_friend_member)
    private ListView mListView;
    private TextView mFooterView;
    /**获取库Phon表字段**/
    private static final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**头像ID**/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**联系人的ID**/
    private static final int PHONES_CONTACT_ID_INDEX = 3;


    /**联系人名称**/
    private ArrayList<String> mContactsName = new ArrayList<>();

    /**联系人头像**/
    private ArrayList<String> mContactsNumber = new ArrayList<>();

    /**联系人头像**/
    private ArrayList<Bitmap> mContactsPhonto = new ArrayList<>();
    /**需要显示的数据 **/
    private ArrayList<Contact> datas = new ArrayList<>();
    private ContactAdapter mAdapter;
    private ImageView back_iv;



    @Override
    public void initWidget() {
        super.initWidget();
        back_iv = (ImageView) findViewById(R.id.back_iv);
        SideBar mSideBar = (SideBar) findViewById(R.id.school_friend_sidrbar);
        TextView mDialog = (TextView) findViewById(R.id.school_friend_dialog);
        EditText mSearchInput = (EditText) findViewById(R.id.school_friend_member_search_input);

        mSideBar.setTextView(mDialog);
        mSideBar.setOnTouchingLetterChangedListener(this);
        mSearchInput.addTextChangedListener(this);

        // 给listView设置adapter
        mFooterView = (TextView) View.inflate(aty, R.layout.item_list_contact_count, null);
        mListView.addFooterView(mFooterView);
        getPhoneContacts();
        for (int i = 0; i < mContactsNumber.size(); i++) {
            Contact data = new Contact();
            data.setName(mContactsName.get(i));
            data.setPinyin(HanziToPinyin.getPinYin(data.getName()));
            data.setHeadView(mContactsPhonto.get(i));
            data.setPhoneNumber(mContactsNumber.get(i));
            datas.add(data);
        }
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mFooterView.setText(datas.size() + "位联系人");
        mAdapter = new ContactAdapter(mListView, datas);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("phoneNumber",mAdapter.getItem(i).getPhoneNumber());
                setResult(Preference.CREATEAV_CONTACTS_REAULT,intent);
                finish();
            }
        });
        setTranslucentStatus();
    }


    /**得到手机通讯录联系人信息**/
    private void getPhoneContacts() {
        ContentResolver resolver = this.getContentResolver();

// 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);


        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                //得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

                //得到联系人头像ID
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

                //得到联系人头像Bitamp
                Bitmap contactPhoto = null;

                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                if(photoid > 0 ) {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                }else {
                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.default_head_rect);
                }

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
                mContactsPhonto.add(contactPhoto);
            }

            phoneCursor.close();
        }
    }

    private void setTranslucentStatus() {
        //判断版本是4.4以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);

            SystemStatusManager tintManager = new SystemStatusManager(this);
            //打开系统状态栏控制
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(R.color.colorAccent);
            tintManager.setStatusBarTintResource(R.color.color_blue_0888ff);//设置背景
            View layoutAll = findViewById(R.id.layoutAll);
            if (layoutAll != null) {
                //设置系统栏需要的内偏移
                layoutAll.setPadding(0, ScreenUtils.getStatusHeight(this), 0, 0);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        ArrayList<Contact> temp = new ArrayList<>();
        for (Contact data : datas) {
            if (data.getName().contains(charSequence) || data.getPinyin().contains(charSequence)) {
                temp.add(data);
            }
        }
        if (mAdapter != null) {
            mAdapter.refresh(temp);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_contacts);
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = 0;
        // 该字母首次出现的位置
        if (mAdapter != null) {
            position = mAdapter.getPositionForSection(s.charAt(0));
        }
        if (position != -1) {
            mListView.setSelection(position);
        } else if (s.contains("#")) {
            mListView.setSelection(0);
        }
    }
}
