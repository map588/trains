# J.A.M.E.S - Train Simulation System

## Overview

J.A.M.E.S. is a comprehensive train system simulation platform developed for the University of Pittsburgh ECE1140 course. This sophisticated simulation environment models real-world train operations with fail-safe features, providing a robust platform for testing train dispatch, control, and safety systems under various operational and failure scenarios.

The system simulates a complete mass transit network with multiple train lines, stations, and safety-critical control systems. It's designed to demonstrate proper engineering practices in safety-critical systems, including fault tolerance, redundancy, and fail-safe operation modes.

## Key Features

- **Real-time Physics Simulation**: Accurate modeling of train dynamics including acceleration, braking, and power management
- **Multi-layer Safety Systems**: PLC-based vital logic, emergency braking, and collision prevention
- **Fault Injection ("Murphy")**: Simulates real-world failures for testing system resilience
- **Hardware-in-the-Loop Support**: Interface with physical train controllers and wayside controllers
- **Scalable Architecture**: Supports multiple train lines (Green, Red, Blue) with dozens of trains
- **Time-scaled Simulation**: Adjustable time multiplier for accelerated testing

## System Architecture

The J.A.M.E.S. system employs a modular architecture with five primary components that communicate through well-defined Java interfaces:

```
┌─────────────────┐
│   CTC Office    │ ← Centralized dispatch and scheduling
└────────┬────────┘
         │
    ┌────▼────┐
    │ Wayside │ ← PLC-based safety logic and routing
    └────┬────┘
         │
  ┌──────▼──────┐
  │ Track Model │ ← Physical track and environment
  └──────┬──────┘
         │
  ┌──────▼──────┐
  │ Train Model │ ← Train physics and systems
  └──────┬──────┘
         │
  ┌──────▼──────────┐
  │ Train Controller│ ← Speed control and operations
  └──────────────────┘
```

## Core Components

### 1. CTC Office (Centralized Traffic Control)
**Location**: `src/main/java/CTCOffice/`

The nerve center of the train system, responsible for:
- **Train Dispatching**: Manages train deployment based on schedules
- **Schedule Management**: Imports and processes CSV schedule files
- **Track Maintenance**: Controls track blocks for maintenance operations
- **System Monitoring**: Real-time visibility of all trains and track conditions
- **Throughput Tracking**: Monitors passenger flow and ticket sales

Key Classes:
- `CTCOfficeImpl`: Core implementation of dispatch logic
- `TrainSchedule`: Schedule parsing and management
- `CTCBlock`: Individual block control and monitoring

### 2. Track Model
**Location**: `src/main/java/trackModel/`

Simulates the physical track infrastructure:
- **Block-based Architecture**: Track divided into discrete blocks with unique properties
- **Track Features**: Stations, switches, railway crossings, and signals
- **Environmental Simulation**: Temperature, weather, and passenger dynamics
- **Failure Modes**: Broken rails, power failures, track circuit failures
- **Beacon System**: Provides location and station information to trains

Key Classes:
- `TrackLine`: Complete track line management (Green/Red/Blue lines)
- `TrackBlock`: Individual block with physical properties
- `StationBlock`, `SwitchBlock`, `CrossingBlock`: Specialized block types

### 3. Wayside Controller
**Location**: `src/main/java/waysideController/`

Implements vital safety logic through PLC programs:
- **PLC Programming**: Uses custom PLC language for safety logic
- **Occupancy Detection**: Tracks train positions across blocks
- **Switch Control**: Safe routing of trains through switches
- **Signal Management**: Controls track signals for safe train spacing
- **Fail-safe Operation**: Defaults to safe states during failures
- **Hardware Integration**: Supports connection to physical PLCs via serial

Key Classes:
- `WaysideControllerImpl`: Software PLC implementation
- `WaysideControllerHW`: Hardware PLC interface
- `PLCProgram`: PLC script parser and executor

PLC Example:
```plc
// Prevent collision - block authority if next block occupied
if occupied[5]
    authority[4] = FALSE
    signal[4] = RED
endif
```

### 4. Train Model
**Location**: `src/main/java/trainModel/`

Simulates individual train physics and systems:
- **Physics Engine**: Realistic acceleration, braking, and power calculations
- **Failure Simulation**: Brake, power, and signal pickup failures
- **Passenger Dynamics**: Boarding, alighting, and capacity management
- **Environmental Systems**: HVAC, lighting, and door control
- **Emergency Systems**: Emergency brake activation and passenger emergency brake

Key Classes:
- `TrainModelImpl`: Core physics and systems implementation
- `TrainModelManager`: GUI and monitoring interface

Physics calculations include:
- Force balance: `F = ma + friction + grade_force`
- Power limits: Maximum power and braking force constraints
- Speed limits: Block-specific and overall speed restrictions

### 5. Train Controller
**Location**: `src/main/java/trainController/`

Manages train operations and speed control:
- **PID Control**: Proportional-Integral speed regulation
- **Automatic/Manual Modes**: Driver-assisted or fully automatic operation
- **Safety Monitoring**: Enforces speed limits and authority
- **Beacon Processing**: Updates position and station information
- **Failure Detection**: Monitors and responds to system failures
- **Hardware Support**: Interfaces with physical train controllers

Key Classes:
- `TrainControllerImpl`: Software controller implementation
- `TrainControllerHW`: Hardware controller interface
- `KTuner`: PID gain optimization

Control Loop:
```java
power = Kp * speed_error + Ki * ∫speed_error dt
if (power > max_power) power = max_power
if (emergency_brake) power = 0
```

## Safety and Fault Tolerance

### Multi-Layer Safety Architecture

1. **Track Level**: Track circuits detect train presence
2. **Wayside Level**: PLC logic prevents unsafe routes
3. **Train Level**: Automatic train protection (ATP)
4. **Controller Level**: Speed and authority enforcement

### Murphy's Law Testing

The system includes "Murphy" - a fault injection system that can introduce:
- **Track Failures**: Broken rails, power outages, circuit failures
- **Train Failures**: Brake failures, power failures, signal failures
- **Communication Failures**: Lost commands, delayed updates

### Fail-Safe Design Principles

- **Default Safe States**: Systems default to stopped/protected states
- **Redundant Detection**: Multiple methods for train location
- **Vital Logic**: Safety-critical logic in isolated PLCs
- **Emergency Override**: Manual emergency stop capabilities

## Simulation Framework

### Time Management
**Location**: `src/main/java/Framework/Simulation/`

- **Base Time Step**: 100ms update cycle
- **Time Multiplier**: Adjustable 1x to 10x speed
- **Synchronized Updates**: All components update in lockstep
- **Real-time Clock**: Simulated time of day for scheduling

### Component Orchestration

The `Main` class coordinates all components:
1. Initialize track infrastructure
2. Start wayside controllers
3. Launch CTC office
4. Begin simulation loop
5. Update all components each time step

## Technical Specifications

### Requirements
- **Java**: Version 17 or higher
- **JavaFX**: Version 17.0.6+ for GUI
- **Memory**: 2GB RAM minimum
- **OS**: Windows, macOS, Linux

### Dependencies
- JavaFX for user interface
- ANTLR4 for PLC parsing
- JSerialComm for hardware interfaces
- SLF4J for logging
- Medusa for gauge displays

### Performance
- Supports 50+ simultaneous trains
- 100ms update resolution
- Real-time visualization
- Configurable time acceleration

## Usage Examples

### Starting the Simulation
```bash
java -jar trains.jar
```

### Dispatching a Train
1. Load a schedule file (CSV format)
2. Select departure time and route
3. Click "Dispatch Train"
4. Monitor progress on track display

### Testing Failure Scenarios
1. Open Track Model interface
2. Select "Murphy" panel
3. Choose failure type (brake, power, signal)
4. Inject failure and observe system response

### Programming PLCs
```plc
// Example: Station stopping logic
if occupied[STATION_BLOCK] and speed[STATION_BLOCK] == 0
    doors[STATION_BLOCK] = OPEN
    timer[STATION_BLOCK] = 30
endif
```

## Testing and Validation

### Unit Tests
- Component isolation tests
- Physics validation
- PLC logic verification

### Integration Tests
- End-to-end train dispatch
- Multi-train scenarios
- Failure recovery

### Performance Tests
- Maximum train capacity
- Time acceleration limits
- GUI responsiveness

## Project Structure

```
trains/
├── src/main/java/
│   ├── Common/          # Shared interfaces
│   ├── CTCOffice/       # Dispatch and control
│   ├── trackModel/      # Track infrastructure
│   ├── trainModel/      # Train physics
│   ├── trainController/ # Train control
│   ├── waysideController/ # Track safety
│   ├── Framework/       # Simulation engine
│   └── Utilities/       # Helper classes
├── src/main/resources/  # FXML layouts, images
├── src/main/antlr/     # PLC grammar and examples
└── docs/               # Documentation

```

## Installation
The installation guide is available here: [Installation Guide](docs/installation_guide.md)

## User Guide
The user guide is available here: [User Guide](docs/user_guide.md)

## Contributing

This is an academic project for ECE1140. For questions or issues, please contact the course instructors.

## License

See LICENSE file for details.