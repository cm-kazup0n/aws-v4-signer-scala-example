package sts

final case class AssumeRoleContext(
    roleArn: String,
    roleSessionName: String
)
