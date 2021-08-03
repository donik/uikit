package kz.citicom.uikit.controllers;

import android.os.Build;

import java.util.Objects;
import java.util.UUID;

public abstract class InternalViewController {
    protected String controllerID;

    public InternalViewController() {
        this.controllerID = UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalViewController that = (InternalViewController) o;
        return controllerID.equals(that.controllerID);
    }

    @Override
    public int hashCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Objects.hash(controllerID);
        }

        int hash = 7;
        for (int i = 0; i < this.controllerID.length(); i++) {
            hash = hash * 31 + this.controllerID.charAt(i);
        }
        return hash;
    }

    protected abstract void deinit();

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        this.deinit();
    }
}
