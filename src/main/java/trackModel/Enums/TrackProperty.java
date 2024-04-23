package trackModel.Enums;


public enum TrackProperty {
 SECTION,
 BLOCKNUMBER,
 BLOCKLENGTH,
 BLOCKGRADE,
 SPEEDLIMIT,

// boolean properties
 ISCROSSING,
 ISUNDERGROUND,
 ISSIGNAL,
 ISSWITCH,
 ISSTATION,
 ISBEACON,
 HASFAILURE,
 ISOCCUPIED,

//labels
 PASSEMBARKED,
 PASSDISEMBARKED,
 TICKETSALES,
 STATUS,
 SWITCHBLOCKID,
 SWITCHMAIN,
 SWITCHALT,
 SWITCHSTATE,
 SIGNALID,
 SIGNALSTATE,
 CROSSINGSTATE,
 TEMPDISPLAY,
 SETBEACON,
 NAMEOFSTATION,
 LIGHT_STATE,
 TRACKHEATER;

 public String string() {
     return switch (this) {
         case SECTION -> "Section";
         case BLOCKNUMBER -> "Block Number";
         case BLOCKLENGTH -> "Block Length";
         case BLOCKGRADE -> "Block Grade";
         case SPEEDLIMIT -> "Speed Limit";
         case ISCROSSING -> "Is Crossing";
         case ISUNDERGROUND -> "Is Underground";
         case ISSIGNAL -> "Is Signal";
         case ISSWITCH -> "Is Switch";
         case ISSTATION -> "Is Station";
         case ISBEACON -> "Is Beacon";
         case HASFAILURE -> "Has Failure";
         case ISOCCUPIED -> "Is Occupied";
         case PASSEMBARKED -> "Passengers Embarked";
         case PASSDISEMBARKED -> "Passengers Disembarked";
         case TICKETSALES -> "Ticket Sales";
         case STATUS -> "Status";
         case SWITCHBLOCKID -> "Switch Block ID";
         case SWITCHMAIN -> "Switch Main";
         case SWITCHALT -> "Switch Alt";
         case SWITCHSTATE -> "Switch State";
         case SIGNALID -> "Signal ID";
         case SIGNALSTATE -> "Signal State";
         case CROSSINGSTATE -> "Crossing State";
         case TEMPDISPLAY -> "Temp Display";
         case SETBEACON -> "Set Beacon";
         case NAMEOFSTATION -> "Name of Station";
         case TRACKHEATER -> "Track Heater";
         case LIGHT_STATE -> "Light State";
     };
 }
}
