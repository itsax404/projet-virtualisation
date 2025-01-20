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
  // count = 5
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

resource "google_dns_record_set" "dns-record" {
	name = google_dns_managed_zone.esirem-zone.name
	type = "A"
	managed_zone = google_dns_managed_zone.esirem-zone
	rrdatas = [google_compute_instance.default.network_interface[0].access_config[0].nat_ip]
}

resource "google_dns_managed_zone" "esirem-zone" {
  name        = "esirem-zone"
  dns_name    = "server.esirem.fr."
  description = "Example DNS zone"
}