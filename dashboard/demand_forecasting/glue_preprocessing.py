import boto3
import os

# Glue job invocation
glue = boto3.client('glue', aws_access_key_id=os.getenv('AWS_ACCESS_KEY'), aws_secret_access_key=os.getenv('AWS_SECRET_KEY'))

def start_glue_job(job_name, job_params):
    """Start AWS Glue ETL job"""
    response = glue.start_job_run(
        JobName=job_name,
        Arguments=job_params
    )
    print(f"Started Glue Job: {response['JobRunId']}")
    return response['JobRunId']
