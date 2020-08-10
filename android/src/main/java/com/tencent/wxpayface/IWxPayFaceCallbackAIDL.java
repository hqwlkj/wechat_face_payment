/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Volumes/SSD/mmpayface_android/app/lib_base/src/main/aidl/com/tencent/wxpayface/IWxPayFaceCallbackAIDL.aidl
 */
package com.tencent.wxpayface;
public interface IWxPayFaceCallbackAIDL extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.tencent.wxpayface.IWxPayFaceCallbackAIDL
{
private static final String DESCRIPTOR = "com.tencent.wxpayface.IWxPayFaceCallbackAIDL";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.tencent.wxpayface.IWxPayFaceCallbackAIDL interface,
 * generating a proxy if needed.
 */
public static com.tencent.wxpayface.IWxPayFaceCallbackAIDL asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.tencent.wxpayface.IWxPayFaceCallbackAIDL))) {
return ((com.tencent.wxpayface.IWxPayFaceCallbackAIDL)iin);
}
return new com.tencent.wxpayface.IWxPayFaceCallbackAIDL.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_response:
{
data.enforceInterface(DESCRIPTOR);
java.util.Map _arg0;
ClassLoader cl = (ClassLoader)this.getClass().getClassLoader();
_arg0 = data.readHashMap(cl);
this.response(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.tencent.wxpayface.IWxPayFaceCallbackAIDL
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
public String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void response(java.util.Map info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeMap(info);
mRemote.transact(Stub.TRANSACTION_response, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_response = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void response(java.util.Map info) throws android.os.RemoteException;
}
