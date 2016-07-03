package com.niu.dialogfragment.widget;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;



/**
 * Created by niuxiaowei on 2016/2/3. http://www.jianshu.com/p/af6499abd5c2#
 * 对话框工厂
 * 经过一步步艰辛的路程，封装DialogFragment的工作终于结束了，封装好的Dialog架构可以给您带来以下好处：

 可以让DialogFragment的使用像Dialog一样的简单、灵活，同时也保持了DialogFragment的优点，可以在任何的类中使用。就像下面代码：

 //某一个Activity中显示ConfirmDialog
 mDialogFactory.showConfirmDialog(title,message,confirmDialogListener);
 //某一个Fragment中显示ConfirmDialog
 mDialogFactory.showConfirmDialog(this,message,confirmDialogListener);
 //非Activity和非Fragment的类中显示ConfirmDialog
 mDialogFactory.showConfirmDialog(this,message,confirmDialogListener);
 很简单的新增新类型的Dialog
 同时在使用的时候需要注意以下几点：

 1 . 在既不是Activity也不是Fragment的类（下面我们简称该类）中调起Dialog要求：

 该类拥有DialogFactory 属性（DialogFactory 的值是从继承了BaseActivity的Activity或继承了BaseFragment的Fragment传递进来的）
 在给DialogFactory 属性赋值后，紧接着需要调用DialogFactory 的restoreDialogListener(Object)方法
 该类实现了XXDialogListener或者该类包含XXDialogListener这样的一个属性（该属性权限必须是public）
 2 .在继承了BaseActivity的Activity（简称activity）中或者继承了BaseFragment的Fragment（简称fragment）中调起Dialog的要求：

 activity或fragment实现了XXDialogListener或者是activity或fragment包含XXDialogListener这样的一个public类型的属性。
 3 .若需要创建新的类型的Dialog，需要注意的是：

 继承BaseDialogFragment
 若该Dialog对外提供接口（接口需要继承BaseDialogListener，需要实现onReceiveDialogListener()方法）
 */
public class DialogFactory {

    /**
     * 进度条的tag
     */
    private static final String DIALOG_PROGRESS_TAG = "progress";

    private static final String DIALOG_CONFIRM_TAG = "confirm";
    private static final String DIALOG_LIST_TAG = "list";

    private FragmentManager mFragmentManager;


    public DialogListenerHolder mListenerHolder = new DialogListenerHolder();

    public DialogFactory(FragmentManager fragmentManager, Bundle savedInstanceState){
        this.mFragmentManager = fragmentManager;
        mListenerHolder.getDialogListenerKey(savedInstanceState);
    }


    /**
     * 这个方法很重要，是恢复dialog listener的一个关键点，在初始化DialogFactory或把DialogFactory赋值后，调用该方法，把调用该方法所在
     * 的类的实例作为参数。 该方法会把param中的属性依次遍历，尝试找属性是BaseDialogFragment.BaseDialogListener的实例，
     * 并且该属性就是保存在bundle中的dialog listener key对应的dialog listener
     * @param o
     */
    public void restoreDialogListener(Object o){
        mListenerHolder.restoreDialogListener(o);
    }


    /**
     * @param message 进度条显示的信息
     * @param cancelable 点击空白处是否可以取消
     */
    public void showProgressDialog(String message, boolean cancelable){
        if(mFragmentManager != null){

            /**
             * 为了不重复显示dialog，在显示对话框之前移除正在显示的对话框。
             */
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            Fragment fragment = mFragmentManager.findFragmentByTag(DIALOG_PROGRESS_TAG);
            if (null != fragment) {
                ft.remove(fragment).commit();
            }

            ProgressDialogFragment progressDialogFragment = ProgressDialogFragment.newInstance(message, cancelable);
            progressDialogFragment.show(mFragmentManager, DIALOG_PROGRESS_TAG);

            mFragmentManager.executePendingTransactions();
        }
    }

    /**
     * 取消进度条
     */
    public void dissProgressDialog(){
        Fragment fragment = mFragmentManager.findFragmentByTag(DIALOG_PROGRESS_TAG);
        if (null != fragment) {
            ((ProgressDialogFragment)fragment).dismiss();
            mFragmentManager.beginTransaction().remove(fragment).commit();
        }
    }

    /**
     *     //显示确认对话框，dialogId是用来区分不同对话框的

     * @param title 对话框title
     * @param message
     * @param cancelable
     * @param listener
     */
    public void showConfirmDialog(String title,String message,boolean cancelable, ConfirmDialogFragment.ConfirmDialogListener listener){

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(DIALOG_CONFIRM_TAG);
        if (null != fragment) {
            ft.remove(fragment);
        }
        DialogFragment df = ConfirmDialogFragment.newInstance(title, message, cancelable);
        df.show(mFragmentManager,DIALOG_CONFIRM_TAG);
        mFragmentManager.executePendingTransactions();

        mListenerHolder.setDialogListener(listener);
    }

    /**
     * 显示列表对话框
     * @param items
     * @param cancelable
     * @param listDialogListener
     */
    public void showListDialog(String[] items,boolean cancelable,ListDialogFragment.ListDialogListener listDialogListener){
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(DIALOG_LIST_TAG);
        if (null != fragment) {
            ft.remove(fragment);
        }
        DialogFragment df = ListDialogFragment.newInstance(items,cancelable);
        df.show(mFragmentManager,DIALOG_LIST_TAG);
        mFragmentManager.executePendingTransactions();

        mListenerHolder.setDialogListener(listDialogListener);
    }
}
