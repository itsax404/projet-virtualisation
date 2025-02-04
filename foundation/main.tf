terraform{
    required_providers {
      scaleway = {
        source = "scaleway/scaleway"
      }
    }
    required_version = ">= 0.13"
}

provider "scaleway" {
    zone = "fr-par-2"
    region = "fr-par"
}

resource "scaleway_vpc" "vpc" {
    name = "calc-vpc"    
    tags = ["demo", "terraform"]
}

resource "scaleway_k8s_cluster" "calc_cluster" {
    name    = "calc-cluster"
    version = "1.29.1"
    cni     = "cilium"
    private_network_id = scaleway_vpc.vpc.id
    delete_additional_resources = false
}

resource "scaleway_k8s_pool" "calc_pool" {
    cluster_id = scaleway_k8s_cluster.calc_cluster.id
    name       = "calc-pool"
    node_type  = "DEV1-M"
    size       = 1
}

resource "scaleway_lb" "calc_load_balancer_prod" {
    name = "calc-lb-prod"
    type = "LB-S"
    ip_ids = [scaleway_instance_ip.calc_public_ip_prod.id]
    private_network {
      private_network_id = scaleway_vpc.vpc.id
    }
}

resource "scaleway_lb" "calc_load_balancer_dev" {
    name = "calc-lb-dev"
    type = "LB-S"
    ip_ids = [scaleway_instance_ip.calc_public_ip_dev.id]
    private_network {
      private_network_id = scaleway_vpc.vpc.id
    }
}

resource "scaleway_instance_ip" "calc_public_ip_prod"{
}

resource "scaleway_instance_ip" "calc_public_ip_dev"{
}

resource "scaleway_domain_record" "calc_dns_prod" {
    dns_zone = "kiowy.net"
    name = "calculatrice-davin-druhet-polytech-dijon"
    type  = "A"
    data = scaleway_lb.calc_load_balancer_prod.ip_address
    ttl = 3600
}

resource "scaleway_domain_record" "calc_dns_dev" {
    dns_zone = "kiowy.net"
    name = "calculatrice-dev-davin-druhet-polytech-dijon"
    type  = "A"
    data = scaleway_lb.calc_load_balancer_dev.ip_address
    ttl = 3600
}

resource "scaleway_rdb_instance" "db-instance-prod" {
    name           = "db-instance-prod"
    node_type      = "DB-DEV-S"
    engine         = "PostgreSQL-15"
    is_ha_cluster  = true
    user_name      = var.database_user_prod
    password       = var.database_password_prod
    encryption_at_rest = true
    private_network {
        pn_id = scaleway_vpc.vpc.id
    }
}

resource "scaleway_rdb_database" "db-database-prod" {
  instance_id    = scaleway_rdb_instance.db-instance-prod.id
  name           = "database"
}

resource "scaleway_rdb_instance" "db-instance-dev" {
    name           = "db-instance-dev"
    node_type      = "DB-DEV-S"
    engine         = "PostgreSQL-15"
    is_ha_cluster  = true
    user_name      = var.database_user_dev
    password       = var.database_password_dev
    encryption_at_rest = true
    private_network {
        pn_id = scaleway_vpc.vpc.id
    }
}

resource "scaleway_rdb_database" "db-database-dev" {
  instance_id    = scaleway_rdb_instance.db-instance-dev.id
  name           = var.database_name
}

resource "scaleway_registry_namespace" "calc-container-registry" {
  name        = "calc-container-registry"
  is_public   = false
}