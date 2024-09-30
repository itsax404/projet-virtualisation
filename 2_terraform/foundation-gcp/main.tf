terraform {
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~>6.0"
    }
  }
}

provider "google" {
  project     = var.project_id
  credentials = "./student.json"
  region      = var.region
}

resource "google_compute_network" "vpc_network" {
  name = "esirem"
}

resource "google_compute_instance" "default" {
  name = "server"
  machine_type = "e2.micro"
  zone= var.region

  boot_disk {
    initialize_params {
      image = "debian-cloud/debian12"
    }
  }

  network_interface {
    network = google_compute_network.vpc_network.id
    access_config {}
  }
}

resource "google_sql_database_instance" "main" {
  name             = "main-database"
  database_version = "MYSQL_8_0"
  region           = var.region

  settings {
    tier = "db-f1-micro"
  }
}

resource "google_dns_record_set" "server" {
  name = "server.${google_dns_managed_zone.prod.dns_name}"
  type = "A"
  ttl  = 300

  managed_zone = google_dns_managed_zone.prod.name

  rrdatas = [google_compute_instance.default.network_interface[0].access_config[0].nat_ip]
}

resource "google_dns_managed_zone" "prod" {
  name     = "prod-zone"
  dns_name = "server.esirem.fr."
}
