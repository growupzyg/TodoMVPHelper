package com.growupzyg.mvphelper;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by ZhangYige on 2016/9/7.
 */
public class SettingUI implements Configurable{
    public static String BASE_PACKAGE_NAME = "basePackageName";//BaseView（or BasePresenter）'s package name
    private JTextField mTextFieldPackageName;
    private JPanel mPanel;

    @Nls
    @Override
    public String getDisplayName() {
        return "TodoMVP";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return mPanel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        String basePackageName = mTextFieldPackageName.getText();
        if(TextUtils.isEmpty(basePackageName)) {
            throw new ConfigurationException("Setting failed ,package name can not be empty", "error");
        }else {
            PropertiesComponent.getInstance().setValue(BASE_PACKAGE_NAME,basePackageName);
        }
    }


    @Override
    public void reset() {
        String basePackageName = PropertiesComponent.getInstance().getValue(SettingUI.BASE_PACKAGE_NAME);
        if(!TextUtils.isEmpty(basePackageName)){
            mTextFieldPackageName.setText(basePackageName);
        }
    }

    @Override
    public void disposeUIResources() {
        //do nothing
    }
}
