package com.kunano.scansell_native.ui.login;

import androidx.lifecycle.ViewModel;

public class LogInViewModel extends ViewModel {
    private LogInViewModelListener logInViewModelListener;

    public void navigateBack(){
        if (logInViewModelListener != null)logInViewModelListener.navigateBack();
    }


    public LogInViewModelListener getLogInViewModelListener() {
        return logInViewModelListener;
    }

    public void setLogInViewModelListener(LogInViewModelListener logInViewModelListener) {
        this.logInViewModelListener = logInViewModelListener;
    }

    public interface LogInViewModelListener{
        public abstract void navigateBack();
    }

}
