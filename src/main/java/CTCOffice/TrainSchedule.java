package CTCOffice;


import Framework.Support.BlockIDs;
import Utilities.Enums.Lines;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

import static CTCOffice.CTCOfficeImpl.*;

public class TrainSchedule {
    private final int trainID;
    private String line;
    private double dispatchTime;
    private int carCount;
    private final ArrayList<TrainStop> stops;
    public final ObservableList<Integer> stopIndices = FXCollections.observableArrayList();
    public final ObservableList<TrainStopSubject> stopList;

    private int stopsCompleted = 0;

    TrainScheduleSubject subject;
    CTCBlockSubjectMap blockSubjectMap = CTCBlockSubjectMap.getInstance();

    private final ArrayList<Integer> TrackLayout;

    public TrainSchedule(int trainID, String line, int dispatchTime, int carCount, ArrayList<TrainStop> stops) {
        this.trainID = trainID;
        this.line = line;
        this.dispatchTime = dispatchTime;
        this.carCount = carCount;
        this.stops = stops;
        stopList = FXCollections.observableArrayList();
        for (TrainStop stop : stops) {
            stopList.add(stop.getSubject());
            stopIndices.add(stop.getStopIndex());
        }
        subject = new TrainScheduleSubject(this);
        TrackLayout = (line.equals("GREEN")) ? GreenTrackLayout : RedTrackLayout;
    }

    public int getTrainID() {
        return trainID;
    }

    public void setDispatchTime(double dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public double getDispatchTime() {
        return dispatchTime;
    }

    public void setCarCount(int carCount) {
        this.carCount = carCount;
    }

    public int getCarCount() {
        return carCount;
    }

    public ArrayList<TrainStop> getStops() {
        return stops;
    }

    public TrainStop getStop(int index) {
        return stops.get(index);
    }

    public int getStopCount() {
        return stops.size();
    }

    public void addStop(int blockID, int arrivalTime, int departureTime) {
        stops.add(new TrainStop(stops.size(), blockID, arrivalTime, departureTime));
        stopList.add(stops.get(stops.size() - 1).getSubject());
        stopIndices.add(stops.size());
    }

    public void removeStop(int index) {
        if (index < 0 || index >= stops.size()) {
            return;
        }
        for (int i = stops.size() - 1; i > index; i--) {
            stops.get(i).setArrivalTime(stops.get(i - 1).getArrivalTime());
            stops.get(i).setDepartureTime(stops.get(i - 1).getDepartureTime());
        }
        stopList.remove(index);
        stops.remove(index);
        stopIndices.remove(stops.size());
        for (int i = index; i < stops.size(); i++) {
            stops.get(i).setStopIndex(i);
        }
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public TrainScheduleSubject getSubject() {
        return subject;
    }

    public int getStopsCompleted() {
        return stopsCompleted;
    }

    public void incrementStopsCompleted() {
        stopsCompleted++;
    }

    public void moveStop(int stopCurrentIndex, int stopNewIndex) {
        TrainStop temp = stops.get(stopCurrentIndex);
        double tempArrival = stops.get(stopNewIndex).getArrivalTime();
        double tempDeparture = stops.get(stopNewIndex).getDepartureTime();
        if (stopCurrentIndex == stopNewIndex) {
            return;
        } else if (stopCurrentIndex < stopNewIndex) {
            for (int i = stopNewIndex; i > stopCurrentIndex; i--) {
                stops.get(i).setArrivalTime(stops.get(i - 1).getArrivalTime());
                stops.get(i).setDepartureTime(stops.get(i - 1).getDepartureTime());
            }
        } else {
            for (int i = stopNewIndex; i < stopCurrentIndex; i++) {
                stops.get(i).setArrivalTime(stops.get(i + 1).getArrivalTime());
                stops.get(i).setDepartureTime(stops.get(i + 1).getDepartureTime());
            }
        }
        stopList.remove(stopCurrentIndex);
        stops.remove(stopCurrentIndex);
        stops.add(stopNewIndex, temp);
        stops.get(stopNewIndex).setStopIndex(stopNewIndex);
        stops.get(stopNewIndex).setArrivalTime(tempArrival);
        stops.get(stopNewIndex).setDepartureTime(tempDeparture);
        stopList.add(stopNewIndex, stops.get(stopNewIndex).getSubject());
        for (int i = 0; i < stops.size(); i++) {
            stops.get(i).setStopIndex(i);
        }
    }

    public int checkSchedule() {
        int visited = 0;
        for (int i = 0; i < stops.size(); i++) {
            // Check if arrival time is before departure time of previous stop
            if (!TrackLayout.contains(stops.get(i).getStationBlockID())) {
                removeStop(i);
                return -1;
            }
            if (i >= 1) {
                if (stops.get(i).getArrivalTime() < stops.get(i - 1).getDepartureTime()) {
                    stops.get(i).setArrivalTime(stops.get(i - 1).getDepartureTime() + 2);
                }
            }
            // Check if arrival time is before departure time
            if (stops.get(i).getArrivalTime() > stops.get(i).getDepartureTime()) {
                stops.get(i).setDepartureTime(stops.get(i).getArrivalTime() + 1);
            }
            // Check if stop is at the same station as last stop
            if (i >= 1) {
                if (stops.get(i).getStationBlockID() == stops.get(i - 1).getStationBlockID()) {
                    System.out.println("Removing stop : " + i);
                    removeStop(i);
                    System.out.println("Removed stop : " + i);
                    return -1;
                }
            }
            // Check if station is out of order
            if (i >= 1) {
                if (TrackLayout.indexOf(stops.get(i).getStationBlockID()) > visited) {
                    visited = TrackLayout.indexOf(stops.get(i).getStationBlockID());
                } else if (TrackLayout.lastIndexOf(stops.get(i).getStationBlockID()) > visited) {
                    visited = TrackLayout.lastIndexOf(stops.get(i).getStationBlockID());
                } else {
                    return i;
                }
            } else {
                visited = TrackLayout.indexOf(stops.get(i).getStationBlockID());
            }
        }
        return -2;
    }

    public void fixSchedule() {
        int fixed;
        while (true) {
            fixed = checkSchedule();
            System.out.println(fixed + " : " + stops.size());
            if (fixed == -2) {
                break;
            }
            if (fixed >= 0) {
                System.out.println("moving stop : " + fixed + " to " + (fixed - 1));
                moveStop(fixed, fixed - 1);
            }
        }
        System.out.println(fixed);
        updateInternalSchedule();
    }

    public void updateInternalSchedule() {
        setAuthorities();
        setSpeeds();
    }

    public void setAuthorities() {
        int visited = 0;
        for (TrainStop stop : stops) {
            stop.getAuthorityList().clear();
            int blocksAuthority = ((TrackLayout.indexOf(stop.getStationBlockID()) < visited) ?
                    (TrackLayout.lastIndexOf(stop.getStationBlockID()) - visited) :
                    (TrackLayout.indexOf(stop.getStationBlockID()) - visited));

            //blocksAuthority += 1;
            int station = TrackLayout.indexOf(stop.getStationBlockID());
            double metersAuthority =  (blockSubjectMap.getSubject(
                    BlockIDs.of(TrackLayout.get(station), Enum.valueOf(Lines.class, line))).getBlockInfo().getLength() / 2);
            //making meters authority
                for (int j = visited; j < TrackLayout.indexOf(stop.getStationBlockID()); j++) {
                    double blockLength = blockSubjectMap.getSubject(
                            BlockIDs.of(TrackLayout.get(j), Enum.valueOf(Lines.class, line))).getBlockInfo().getLength();
                    metersAuthority += blockLength;
                    System.out.println("Adding " + blockLength + " to authority of stop " + stop.getStopIndex() + " of train " + trainID);
                }
            for (int j = 0; j <= blocksAuthority; j++) {
                double blockDistance =
                        blockSubjectMap.getSubject(BlockIDs.of(TrackLayout.get(visited + j), Enum.valueOf(Lines.class, line)))
                                .getBlockInfo().getLength();
                stop.getAuthorityList().add(metersAuthority);
                stop.getRoutePath().add(TrackLayout.get(visited + j));
                System.out.println("Authority : "
                        + stop.getAuthorityList().get(j)
                        + " set for the " + j + "th block of stop "
                        + stop.getStopIndex() + " of train "
                        + trainID + " with block "
                        + stop.getRoutePath().get(j)
                );
                metersAuthority -= blockDistance;
            }
            visited += blocksAuthority;
        }
    }

    public void setSpeeds() {
        for (TrainStop stop : stops) {
            double scheduledTraversalTime =
                    (stop.getStopIndex() == 0) ?
                            (stop.getArrivalTime() - dispatchTime) :
                            (stop.getArrivalTime() - stops.get(stop.getStopIndex() - 1).getDepartureTime());
            double minTraversalTime = 0.0;
            stop.getSpeedList().clear();
            for (int j = 0; j < stop.getRoutePath().size(); j++) {
                stop.getSpeedList().add((blockSubjectMap.getSubject(BlockIDs.of(stop.getRoutePath().get(j), Enum.valueOf(Lines.class, line))).getBlockInfo().getSpeedLimit() * 0.277778));
                minTraversalTime +=
                        (blockSubjectMap.getSubject(
                                  BlockIDs.of(stop.getRoutePath().get(j), Enum.valueOf(Lines.class, line)))
                                  .getBlockInfo().getLength())
                        / /*< --- Dividing ---->*/
                        (blockSubjectMap.getSubject(
                                BlockIDs.of(stop.getRoutePath().get(j), Enum.valueOf(Lines.class, line)))
                                  .getBlockInfo().getSpeedLimit()   * 0.277778);
            }
            if(scheduledTraversalTime < minTraversalTime) {
                if(stop.getStopIndex() == 0) {
                    stop.setArrivalTime(dispatchTime + minTraversalTime);
                } else {
                    stop.setArrivalTime(stops.get(stop.getStopIndex() - 1).getDepartureTime() + minTraversalTime);
                    stop.setDepartureTime(stop.getArrivalTime() + 60);
                }
            }
        }
    }

}