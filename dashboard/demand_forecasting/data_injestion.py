import boto3
import os

s3 = boto3.client('s3', aws_access_key_id=os.getenv('AWS_ACCESS_KEY'), aws_secret_access_key=os.getenv('AWS_SECRET_KEY'))

def upload_to_s3(file_name, bucket, object_name=None):
    """Upload a file to S3 bucket"""
    try:
        s3.upload_file(file_name, bucket, object_name or file_name)
        print(f"File uploaded to S3: {object_name}")
    except Exception as e:
        print(f"Error uploading to S3: {e}")
