import boto3
import os 
sagemaker = boto3.client('sagemaker', aws_access_key_id=os.getenv('AWS_ACCESS_KEY'), aws_secret_access_key=os.getenv('AWS_SECRET_KEY'))

def start_training_job(training_job_name, model_data, output_path):
    """Start a SageMaker training job"""
    response = sagemaker.create_training_job(
        TrainingJobName=training_job_name,
        AlgorithmSpecification={
            'TrainingImage': 'sagemaker-sarima-container',  # Custom container
            'TrainingInputMode': 'File'
        },
        InputDataConfig=[
            {
                'ChannelName': 'training',
                'DataSource': {
                    'S3DataSource': {
                        'S3Uri': model_data,
                        'S3DataType': 'S3Prefix',
                        'S3DataDistributionType': 'FullyReplicated'
                    }
                }
            }
        ],
        OutputDataConfig={'S3OutputPath': output_path},
        ResourceConfig={
            'InstanceType': 'ml.m5.large',
            'InstanceCount': 1,
            'VolumeSizeInGB': 20
        },
        StoppingCondition={'MaxRuntimeInSeconds': 3600},
        RoleArn=os.getenv('AWS_ROLE_ARN')
    )
    print(f"Started Training Job: {response['TrainingJobArn']}")
    return response['TrainingJobArn']
