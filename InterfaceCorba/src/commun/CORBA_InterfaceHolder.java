package commun;

/**
* commun/CORBA_InterfaceHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from CORBA_Interface.idl
* domingo 10 de abril de 2016 03:55:12 PM CDT
*/

public final class CORBA_InterfaceHolder implements org.omg.CORBA.portable.Streamable
{
  public commun.CORBA_Interface value = null;

  public CORBA_InterfaceHolder ()
  {
  }

  public CORBA_InterfaceHolder (commun.CORBA_Interface initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = commun.CORBA_InterfaceHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    commun.CORBA_InterfaceHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return commun.CORBA_InterfaceHelper.type ();
  }

}
