import os
from pymongo import MongoClient
from dotenv import load_dotenv
load_dotenv()
MONGO_URI = "mongodb+srv://aryannvr:aryan181527@cluster0.pgxdqvd.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"

# MongoDB Configuration
try:
    client = MongoClient(MONGO_URI, serverSelectionTimeoutMS=5000)  # 5 seconds timeout
    db = client["grievance_calls"]
    calls_collection = db["calls"]
    print("MongoDB connected successfully")

    # Sample data to insert
    sample_data = {
        "caller_name": "John Doe",
        "caller_id": "12345",
        "issue": "Sample issue for testing",
        "timestamp": "2023-10-01T12:00:00Z"
    }

    # Insert sample data
    result = calls_collection.insert_one(sample_data)
    print(f"Sample data inserted with ID: {result.inserted_id}")

except Exception as e:
    print(f"Failed to connect to MongoDB: {e}")