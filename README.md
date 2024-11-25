## ☀️ Overview

This repository contains a **demand forecasting platform** built using **FastAPI**, integrating a variety of tools like **LLM Models**, **AWS services (S3, Glue, SageMaker)**, and **PostgreSQL** for dynamic forecasting and data handling. The platform supports functionalities such as SQL query handling, inventory forecasting, and help-based assistance through AI-powered endpoints. The backend is modularized for scalability, with utilities for data ingestion, ETL jobs, and model training.

## 📐Architecture

![WhatsApp Image 2024-11-25 at 22 33 00_1f258aa8](https://github.com/user-attachments/assets/0a3d9308-44f7-4bc5-b04c-3ccb730d5166)


## 🚀 Steps to Run the Application

### Prerequisites
- Python 3.8 or higher
- PostgreSQL database
- AWS credentials configured
- `.env` file with required environment variables (see `.env.example` for reference).

### Installation

1. Clone the repository:
   ```bash
   git clone <repository_url>
   cd <repository_directory>
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
   - `LLM_KEY`: LLM API key of your choice.
   - `POSTGRES_URL`: PostgreSQL connection URI.
   - `AWS_ACCESS_KEY`, `AWS_SECRET_KEY`, `AWS_ROLE_ARN`: AWS credentials for S3, Glue, and SageMaker.

### Run the Application
1. Start the FastAPI server:
   ```bash
   uvicorn app:app --reload
   ```

2. Access the application at: `http://127.0.0.1:8000`.

3. Optionally, you can test endpoints using tools like **Postman** or the FastAPI Swagger UI at `http://127.0.0.1:8000/docs`.

## ⚙️ Tech Stack

### Backend Framework
- **FastAPI**: Core web framework for creating APIs and handling requests.

### Libraries and Tools
- **LangChain**: Used to integrate OpenAI's LLM for natural language processing.
- **Pydantic**: For data validation and structured response models.
- **SQLAlchemy**: For database integration with PostgreSQL.
- **Boto3**: For interaction with AWS services like S3, Glue, and SageMaker.
- **Requests**: For sending API calls (e.g., to SageMaker endpoint).

### Cloud Services
- **AWS S3**: Storage for large datasets.
- **AWS Glue**: ETL jobs for preprocessing data.
- **AWS SageMaker**: For training and hosting machine learning models.

### Routes and Functionalities

1. **SQL Query Endpoint**:
   - **Route**: `/chat`
   - **Description**: Handles user queries to execute SQL commands, process results using Agents in Langchain, and return natural language summaries in JSON format.
   - **Tech Stack**: SQL Agents in Langchain, PostgreSQL, LangChain.

2. **Help Query Endpoint**:
   - **Route**: `/help`
   - **Description**: Provides detailed guidance (steps and images) for predefined help categories using a JSON data structure.
   - **Tech Stack**: JSON Agents in Langchain, JSON-based static data.

3. **Forecast API**:
   - **Route**: `/api/get-forecast` and `/api/forecast`
   - **Description**: Fetches and analyzes inventory forecasts for a given product using AWS SageMaker and custom logic for recommendations.
   - **Tech Stack**: AWS SageMaker, FastAPI.

4. **Data Ingestion Utility**:
   - **Module**: `data_injestion.py`
   - **Functionality**: Uploads datasets to S3 for processing.
   - **Tech Stack**: AWS S3, Boto3.

5. **ETL Job Utility**:
   - **Module**: `glue_preprocessing.py`
   - **Functionality**: Invokes AWS Glue jobs for ETL processing of data.
   - **Tech Stack**: AWS Glue, Boto3.

6. **Model Training Utility**:
   - **Module**: `model_train.py`
   - **Functionality**: Triggers SageMaker training jobs with the provided configuration.
   - **Tech Stack**: AWS SageMaker, Boto3.

## Future Enhancements
- Integration of **GDPR** and **HIPAA** compliance scoring for sensitive data handling.
- Admin dashboard for monitoring forecast metrics and file approvals.
- CI/CD pipelines for automated deployment using tools like **Jenkins**.

Feel free to reach out for any questions or contributions! 🚀
