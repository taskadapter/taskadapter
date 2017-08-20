# Loading native fields

* Assignees, Reporters, etc should be stored in GTask as GUser instances.
This is because some connectors will require login names, some - user ids only.
 
## Multi-value fields

Set values in GTask as Seq[String]

