variable "project_id"{
    default = "calculatrice"
}

variable "database_user_prod"{
    default = "admin"
}

variable "database_password_prod"{
    default = "admin"
    sensitive = true
}

variable "database_user_dev"{
    default = "admin_dev"
}

variable "database_password_dev"{
    default = "admin"
    sensitive = true
}

variable "database_name"{
    default = "database"
}