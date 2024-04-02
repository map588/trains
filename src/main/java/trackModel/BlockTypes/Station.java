package trackModel.BlockTypes;

 public interface Station{
     String getStationName();
     String getDoorDirection();
     int getPassengersWaiting();
     void setPassengersWaiting(int passengersWaiting);
     int getPassengersEmbarked();
     void setPassengersEmbarked(int passengersEmbarked);
     int getPassengersDisembarked();
     void setPassengersDisembarked(int passengersDisembarked);
}
