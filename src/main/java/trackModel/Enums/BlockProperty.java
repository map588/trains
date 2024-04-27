package trackModel.Enums;

public enum BlockProperty {
  DIRECTION,
  BLOCKLENGTH,
  SPEEDLIMIT,
  ISSTATION,
  ISBEACON,
  BLOCKNUMBER,
  SWITCHSTATE,
  OCCUPANCY,
  HASFAILURE,
  CIRCUITFAILURE,
  POWERFAILURE,
  RAILFAILURE;

  String string() {
    return switch (this) {
      case DIRECTION -> "Direction";
      case BLOCKLENGTH -> "Block Length";
      case SPEEDLIMIT -> "Speed Limit";
      case ISSTATION -> "Is Station";
      case ISBEACON -> "Is Beacon";
      case BLOCKNUMBER -> "Block Number";
      case SWITCHSTATE -> "Switch State";
      case OCCUPANCY -> "Occupancy";
      case HASFAILURE -> "Has Failure";
      case CIRCUITFAILURE -> "Circuit Failure";
      case POWERFAILURE -> "Power Failure";
      case RAILFAILURE -> "Rail Failure";
    };
  }
}
