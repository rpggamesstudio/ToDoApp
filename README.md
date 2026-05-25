# ToDoApp

A robust and intuitive task management application designed to help users efficiently track and organize their daily activities. Built with a focus on clean object-oriented code and a responsive desktop user interface, this project serves as a showcase of end-to-end application development.

## 🚀 Features

* **Task Lifecycle Management:** Seamlessly add, edit, and remove tasks from your daily list.
* **Persistent Local Storage:** Tasks are automatically serialized and saved locally to `tasks.json`, ensuring data is consistently retained between application sessions.
* **Modern User Interface:** A clean, responsive desktop interface designed to be user-friendly and intuitive.
* **Cross-Platform:** Designed to run efficiently on any operating system supporting the Java Runtime Environment (JRE).

## 🛠️ Technologies Used

* **Language:** Java
* **UI Framework:** JavaFX / FXML
* **Build Tool:** Maven
* **Data Management:** JSON Serialization

## ⚙️ Setup and Installation

### Prerequisites
* Java Development Kit (JDK) 11 or higher installed on your local machine.

### Running the Application
You can run this application without needing to install Maven globally, as the repository utilizes the Maven Wrapper.

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/rpggamesstudio/ToDoApp.git](https://github.com/rpggamesstudio/ToDoApp.git)
   cd ToDoApp
2. Build and run:
   on Windows:  mvnw.cmd clean javafx:run
   on Linux/macOS: ./mvnw clean javafx:run
