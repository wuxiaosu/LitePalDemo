package com.wuxiaosu.litepal_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wuxiaosu.litepal_demo.adapter.BaseAdapter;
import com.wuxiaosu.litepal_demo.adapter.UserAdapter;
import com.wuxiaosu.litepal_demo.model.User;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtId;
    private EditText mEtName;
    private EditText mEtSex;
    private EditText mEtAge;

    private String id;
    private String name;
    private String sex;
    private String age;

    private RecyclerView mRvContent;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mEtId = (EditText) findViewById(R.id.et_id);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtSex = (EditText) findViewById(R.id.et_sex);
        mEtAge = (EditText) findViewById(R.id.et_age);
        Button mBtnInsert = (Button) findViewById(R.id.btn_insert);
        Button mBtnDelete = (Button) findViewById(R.id.btn_delete);
        Button mBtnUpdate = (Button) findViewById(R.id.btn_update);
        Button mBtnSelect = (Button) findViewById(R.id.btn_select);

        mBtnInsert.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mBtnUpdate.setOnClickListener(this);
        mBtnSelect.setOnClickListener(this);
        initRecycleView();
    }

    private void initRecycleView() {
        mRvContent = (RecyclerView) findViewById(R.id.rv_content);
        mRvContent.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, new ArrayList<User>());
        userAdapter.setItemClickLister(new BaseAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                User user = userAdapter.getList().get(position);
                if (user != null) {
                    mEtId.setText(String.valueOf(user.getId()));
                    mEtName.setText(user.getName());
                    mEtSex.setText(user.getSex());
                    mEtAge.setText(user.getAge());
                }
            }
        });
        mRvContent.setAdapter(userAdapter);
        initData();
    }

    private void initData() {
        userAdapter.clear();
        userAdapter.addAll(DataSupport.findAll(User.class));
    }

    @Override
    public void onClick(View view) {
        id = mEtId.getText().toString();
        name = mEtName.getText().toString();
        sex = mEtSex.getText().toString();
        age = mEtAge.getText().toString();
        switch (view.getId()) {
            case R.id.btn_insert:
                notify(insertData());
                initData();
                break;
            case R.id.btn_delete:
                notify(deleteData());
                initData();
                break;
            case R.id.btn_update:
                notify(updateData());
                initData();
                break;
            case R.id.btn_select:
                notify(selectData());
                break;
        }
    }

    private boolean insertData() {
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(sex) && TextUtils.isEmpty(age)) {
            return false;
        }
        User user = new User();
        user.setName(name);
        user.setSex(sex);
        user.setAge(age);
        return user.save();
    }

    private boolean deleteData() {
        if (TextUtils.isEmpty(id) && TextUtils.isEmpty(name) && TextUtils.isEmpty(sex) && TextUtils.isEmpty(age)) {
            return false;
        }
        if (!TextUtils.isEmpty(id)) {
            DataSupport.delete(User.class, Integer.valueOf(id));
            return true;
        }
        if (!TextUtils.isEmpty(name)) {
            DataSupport.deleteAll(User.class, "name = ? ", name);
            return true;
        }
        if (!TextUtils.isEmpty(sex)) {
            DataSupport.deleteAll(User.class, "sex = ? ", sex);
            return true;
        }
        if (!TextUtils.isEmpty(age)) {
            DataSupport.deleteAll(User.class, "age = ? ", age);
        }
        return true;
    }

    private boolean updateData() {
        if (TextUtils.isEmpty(id)) {
            return false;
        }
        User user = DataSupport.find(User.class, Integer.valueOf(id));
        if (user == null) {
            return false;
        }
        user.setName(name);
        user.setSex(sex);
        user.setAge(age);
        user.update(Integer.valueOf(id));
        return true;
    }

    private boolean selectData() {
        if (!TextUtils.isEmpty(id)) {
            userAdapter.clear();
            User user = DataSupport.find(User.class, Integer.valueOf(id));
            if (user != null) {
                userAdapter.add(user);
            }
            return true;
        }
        if (TextUtils.isEmpty(id) && TextUtils.isEmpty(name) && TextUtils.isEmpty(sex) && TextUtils.isEmpty(age)) {
            initData();
            return false;
        }

        StringBuilder sql = new StringBuilder(" 1 = 1 ");
        if (!TextUtils.isEmpty(name)) {
            sql.append(" and name like '%").append(name).append("%'");
        }
        if (!TextUtils.isEmpty(sex)) {
            sql.append(" and sex like '%").append(sex).append("%'");
        }
        if (!TextUtils.isEmpty(age)) {
            sql.append(" and age like '%").append(age).append("%'");
        }
        userAdapter.clear();
        userAdapter.addAll(DataSupport.where(sql.toString()).find(User.class));
        return true;
    }

    private void notify(boolean isSuccess) {
        if (isSuccess) {
            Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
    }
}
