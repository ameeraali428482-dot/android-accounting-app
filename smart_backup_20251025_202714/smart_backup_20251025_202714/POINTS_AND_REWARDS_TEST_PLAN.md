_Markdown_
# Test Plan: Points and Rewards System

## 1. Introduction

This document outlines the test plan for the Points and Rewards system in the Android application. The purpose of this testing is to ensure that the system is fully functional, reliable, and meets all specified requirements.

## 2. Test Objectives

- Verify that points can be added and redeemed correctly.
- Ensure that rewards can be created, updated, and deleted.
- Validate that user rewards are tracked accurately.
- Confirm that the UI is intuitive and user-friendly.
- Test the integration of the Points and Rewards system with the rest of the application.

## 3. Test Scope

### 3.1. In-Scope Features

- PointTransaction management (creation, viewing)
- Reward management (creation, editing, deletion)
- UserReward management (creation, viewing)
- Point calculation logic
- Reward redemption logic
- UI for all Points and Rewards screens

### 3.2. Out-of-Scope Features

- AI analysis features
- Chat system
- Data synchronization with a cloud server

## 4. Test Cases

| Test Case ID | Feature | Test Description | Expected Result |
| :--- | :--- | :--- | :--- |
| **TC-001** | PointTransaction | Add points to a user. | A new PointTransaction is created with the correct number of points and type "EARN". |
| **TC-002** | PointTransaction | View the list of point transactions. | All point transactions for the current company are displayed correctly. |
| **TC-003** | Reward | Create a new reward. | The new reward is saved to the database and appears in the reward list. |
| **TC-004** | Reward | Edit an existing reward. | The changes to the reward are saved correctly. |
| **TC-005** | Reward | Delete a reward. | The reward is removed from the database and the reward list. |
| **TC-006** | UserReward | Redeem a reward for a user with sufficient points. | A new UserReward is created, and the user's points are deducted. |
| **TC-007** | UserReward | Attempt to redeem a reward for a user with insufficient points. | The redemption fails, and an error message is displayed. |
| **TC-008** | UserReward | View the list of user rewards. | All user rewards for the current company are displayed correctly. |
| **TC-009** | UI | Navigate to the Points and Rewards screens from the main menu. | The correct screens are displayed. |
| **TC-010** | UI | Test the date pickers in the detail screens. | The date pickers function correctly and set the dates properly. |

## 5. Test Environment

- **Device:** Android Emulator (Pixel 6, API 33)
- **OS:** Android 13
- **Database:** SQLite (Room)

## 6. Test Execution

Testing will be performed manually based on the test cases outlined above. Any defects found will be logged and tracked until they are resolved.

