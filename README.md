# amazon-sambhav

### To clean & rebuild the project in case of dependency collision (TODO: CLEAN CACHE COMPLETELY)

```bash
mvn clean
mvn dependency:purge-local-repository
mvn install
```


## Overview for Voice Agent ( Grievance Assistance)

This repository provides a **Grievance Call Management System** for **Shopify Sellers** using **Bland AI** for automated call handling and **MongoDB** for call data storage. The application includes endpoints to initiate grievance calls and retrieve call details, along with priority analysis

## Features
1. **Grievance Call Requests**: Sellers can initiate grievance calls handled by Bland AI.
2. **Call Details Retrieval**: Fetch detailed information about a specific call, including priority scoring based on call summaries.
3. **MongoDB Integration**: Stores call data, including metadata and analysis, for future reference.

## Steps to Run the voice-agent

### Prerequisites
- Python 3.8 or higher.
- MongoDB Atlas or a locally configured MongoDB instance.
- Valid API keys for **Bland AI** and **LLM**.
- `.env` file with required environment variables (refer to `.env.example`).

### Installation

1. Clone the repository:
   ```bash
   git clone <repository_url>
   cd <repository_directory>
   cd voice-agent
   ```

2. Create a virtual environment:
   ```bash
   python3 -m venv venv
   source venv/bin/activate   # For Linux/MacOS
   venv\Scripts\activate      # For Windows
   ```

3. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

4. Add your environment variables to `.env`:
   - `LLM_API_KEY`: LLM API key.
   - `BLANDAI_API_KEY`: Bland AI API key.

### Run the voice-agent
1. Start the Flask server:
   ```bash
   python app.py
   ```

2. Access the voice-agent at: `http://127.0.0.1:5000`.

### Example Requests

#### 1. **Initiate a Grievance Call**
- **Endpoint**: `/request-call`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
      "user_id": "12345",
      "phone_number": "+1234567890"
  }
  ```
- **Response**:
  ```json
  {
      "message": "Call successfully initiated",
      "response_data": {
          "batch_id": "batch123",
          "call_id": "call123",
          "message": "Call initiated successfully",
          "status": "success"
      }
  }
  ```

#### 2. **Fetch Call Details**
- **Endpoint**: `/call-details/<call_id>`
- **Method**: `GET`
- **Response**:
  ```json
  {
      "call_id": "call123",
      "duration": "5 minutes",
      "state": "California",
      "city": "San Francisco",
      "transcript_summary": "Customer reported delays in order fulfillment.",
      "priority": 8,
      "transcript": "Full transcript of the call."
  }
  ```

## Tech Stack

### Backend Framework
- **Flask**: For building RESTful APIs.

### Libraries and Tools
- **MongoDB (Pymongo)**: To store and manage call data.
- **Requests**: For interacting with Bland AI's API.
- **LLM**: To analyze grievance summaries and assign priority.
- **dotenv**: To manage environment variables securely.

### Routes and Functionalities

1. **Grievance Call Request**:
   - **Route**: `/request-call`
   - **Description**: Initiates a grievance call for Shopify sellers using Bland AI.
   - **Tech Stack**: Bland AI API, Flask.

2. **Call Details Retrieval**:
   - **Route**: `/call-details/<call_id>`
   - **Description**: Fetches call details from Bland AI, including transcript summaries and calculates priority using LLM.
   - **Tech Stack**: Bland AI API, LLM API, MongoDB.

## Future Enhancements
- Implement user authentication for secure access.
- Add pagination and filtering for retrieving past calls.
- Integrate analytics dashboards for tracking call metrics and performance.

Feel free to reach out with any questions or suggestions! 🚀