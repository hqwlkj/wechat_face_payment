/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/frost/wxworkspace/mmpayface_android/app/lib_base/src/main/aidl/com/tencent/wxpayface/IWxPayFaceAIDL.aidl
 */
package com.tencent.wxpayface;
public interface IWxPayFaceAIDL extends android.os.IInterface
{
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements com.tencent.wxpayface.IWxPayFaceAIDL
    {
        private static final java.lang.String DESCRIPTOR = "com.tencent.wxpayface.IWxPayFaceAIDL";
        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }
        /**
         * Cast an IBinder object into an com.tencent.wxpayface.IWxPayFaceAIDL interface,
         * generating a proxy if needed.
         */
        public static com.tencent.wxpayface.IWxPayFaceAIDL asInterface(android.os.IBinder obj)
        {
            if ((obj==null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin!=null)&&(iin instanceof com.tencent.wxpayface.IWxPayFaceAIDL))) {
                return ((com.tencent.wxpayface.IWxPayFaceAIDL)iin);
            }
            return new com.tencent.wxpayface.IWxPayFaceAIDL.Stub.Proxy(obj);
        }
        @Override public android.os.IBinder asBinder()
        {
            return this;
        }
        @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
            java.lang.String descriptor = DESCRIPTOR;
            switch (code)
            {
                case INTERFACE_TRANSACTION:
                {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_initWxpayface:
                {
                    data.enforceInterface(descriptor);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg0;
                    _arg0 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.initWxpayface(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getWxpayfaceRawdata:
                {
                    data.enforceInterface(descriptor);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg0;
                    _arg0 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.getWxpayfaceRawdata(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getWxpayfaceCode:
                {
                    data.enforceInterface(descriptor);
                    java.util.Map _arg0;
                    java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
                    _arg0 = data.readHashMap(cl);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg1;
                    _arg1 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.getWxpayfaceCode(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_updateWxpayfacePayResult:
                {
                    data.enforceInterface(descriptor);
                    java.util.Map _arg0;
                    java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
                    _arg0 = data.readHashMap(cl);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg1;
                    _arg1 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.updateWxpayfacePayResult(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_reportInfo:
                {
                    data.enforceInterface(descriptor);
                    java.util.Map _arg0;
                    java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
                    _arg0 = data.readHashMap(cl);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg1;
                    _arg1 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.reportInfo(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_reportOrder:
                {
                    data.enforceInterface(descriptor);
                    java.util.Map _arg0;
                    java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
                    _arg0 = data.readHashMap(cl);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg1;
                    _arg1 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.reportOrder(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_releaseWxpayface:
                {
                    data.enforceInterface(descriptor);
                    this.releaseWxpayface();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getWxpayfaceUserInfo:
                {
                    data.enforceInterface(descriptor);
                    java.util.Map _arg0;
                    java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
                    _arg0 = data.readHashMap(cl);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg1;
                    _arg1 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.getWxpayfaceUserInfo(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_stopWxpayface:
                {
                    data.enforceInterface(descriptor);
                    java.util.Map _arg0;
                    java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
                    _arg0 = data.readHashMap(cl);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg1;
                    _arg1 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.stopWxpayface(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_updateWxpayfaceBannerState:
                {
                    data.enforceInterface(descriptor);
                    java.util.Map _arg0;
                    java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
                    _arg0 = data.readHashMap(cl);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg1;
                    _arg1 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.updateWxpayfaceBannerState(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_ttsSpeak:
                {
                    data.enforceInterface(descriptor);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.ttsSpeak(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_startCodeScanner:
                {
                    data.enforceInterface(descriptor);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg0;
                    _arg0 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.startCodeScanner(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_stopCodeScanner:
                {
                    data.enforceInterface(descriptor);
                    this.stopCodeScanner();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getWxpayAuth:
                {
                    data.enforceInterface(descriptor);
                    java.util.Map _arg0;
                    java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
                    _arg0 = data.readHashMap(cl);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg1;
                    _arg1 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.getWxpayAuth(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_dispatchBindApi:
                {
                    data.enforceInterface(descriptor);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.util.Map _arg1;
                    java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
                    _arg1 = data.readHashMap(cl);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg2;
                    _arg2 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.dispatchBindApi(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_dispatchBindApiWithTwoCb:
                {
                    data.enforceInterface(descriptor);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.util.Map _arg1;
                    java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
                    _arg1 = data.readHashMap(cl);
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg2;
                    _arg2 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    com.tencent.wxpayface.IWxPayFaceCallbackAIDL _arg3;
                    _arg3 = com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.asInterface(data.readStrongBinder());
                    this.dispatchBindApiWithTwoCb(_arg0, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                }
                default:
                {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }
        private static class Proxy implements com.tencent.wxpayface.IWxPayFaceAIDL
        {
            private android.os.IBinder mRemote;
            Proxy(android.os.IBinder remote)
            {
                mRemote = remote;
            }
            @Override public android.os.IBinder asBinder()
            {
                return mRemote;
            }
            public java.lang.String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }
            @Override public void initWxpayface(com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_initWxpayface, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void getWxpayfaceRawdata(com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_getWxpayfaceRawdata, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void getWxpayfaceCode(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeMap(info);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_getWxpayfaceCode, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void updateWxpayfacePayResult(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeMap(info);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_updateWxpayfacePayResult, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void reportInfo(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeMap(info);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_reportInfo, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void reportOrder(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeMap(info);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_reportOrder, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void releaseWxpayface() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_releaseWxpayface, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void getWxpayfaceUserInfo(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeMap(info);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_getWxpayfaceUserInfo, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void stopWxpayface(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeMap(info);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_stopWxpayface, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void updateWxpayfaceBannerState(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeMap(info);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_updateWxpayfaceBannerState, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void ttsSpeak(java.lang.String text) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(text);
                    mRemote.transact(Stub.TRANSACTION_ttsSpeak, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
// sdk模式支持扫码

            @Override public void startCodeScanner(com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_startCodeScanner, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void stopCodeScanner() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_stopCodeScanner, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
//实名认证

            @Override public void getWxpayAuth(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeMap(info);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_getWxpayAuth, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
//这个方法一定在第15个api，后续尽量不要新增任何接口，直接使用这个方法来统一处理，AIDL本身对方法有一个固定偏移，调整顺序容易出现新旧版本调用不兼容问题；
//传入String是为了避免int的偏移导致新旧版本商户开发使用api不兼容

            @Override public void dispatchBindApi(java.lang.String apiName, java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(apiName);
                    _data.writeMap(info);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_dispatchBindApi, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            @Override public void dispatchBindApiWithTwoCb(java.lang.String apiName, java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb, com.tencent.wxpayface.IWxPayFaceCallbackAIDL secondCb) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(apiName);
                    _data.writeMap(info);
                    _data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
                    _data.writeStrongBinder((((secondCb!=null))?(secondCb.asBinder()):(null)));
                    mRemote.transact(Stub.TRANSACTION_dispatchBindApiWithTwoCb, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
        static final int TRANSACTION_initWxpayface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_getWxpayfaceRawdata = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_getWxpayfaceCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_updateWxpayfacePayResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
        static final int TRANSACTION_reportInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
        static final int TRANSACTION_reportOrder = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
        static final int TRANSACTION_releaseWxpayface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
        static final int TRANSACTION_getWxpayfaceUserInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
        static final int TRANSACTION_stopWxpayface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
        static final int TRANSACTION_updateWxpayfaceBannerState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
        static final int TRANSACTION_ttsSpeak = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
        static final int TRANSACTION_startCodeScanner = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
        static final int TRANSACTION_stopCodeScanner = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
        static final int TRANSACTION_getWxpayAuth = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
        static final int TRANSACTION_dispatchBindApi = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
        static final int TRANSACTION_dispatchBindApiWithTwoCb = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
    }
    public void initWxpayface(com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException;
    public void getWxpayfaceRawdata(com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException;
    public void getWxpayfaceCode(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException;
    public void updateWxpayfacePayResult(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException;
    public void reportInfo(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException;
    public void reportOrder(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException;
    public void releaseWxpayface() throws android.os.RemoteException;
    public void getWxpayfaceUserInfo(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException;
    public void stopWxpayface(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException;
    public void updateWxpayfaceBannerState(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException;
    public void ttsSpeak(java.lang.String text) throws android.os.RemoteException;
// sdk模式支持扫码

    public void startCodeScanner(com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException;
    public void stopCodeScanner() throws android.os.RemoteException;
//实名认证

    public void getWxpayAuth(java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException;
//这个方法一定在第15个api，后续尽量不要新增任何接口，直接使用这个方法来统一处理，AIDL本身对方法有一个固定偏移，调整顺序容易出现新旧版本调用不兼容问题；
//传入String是为了避免int的偏移导致新旧版本商户开发使用api不兼容

    public void dispatchBindApi(java.lang.String apiName, java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb) throws android.os.RemoteException;
    public void dispatchBindApiWithTwoCb(java.lang.String apiName, java.util.Map info, com.tencent.wxpayface.IWxPayFaceCallbackAIDL cb, com.tencent.wxpayface.IWxPayFaceCallbackAIDL secondCb) throws android.os.RemoteException;
}
