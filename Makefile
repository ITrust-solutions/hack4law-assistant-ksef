#IMAGE_NAME=europe-central2-docker.pkg.dev/phonic-monolith-310711/wedeliverit/native-image
IMAGE_NAME=hack4law.azurecr.io/hack4law-assistant-ksef
IMAGE_VERSION=0.4

docker:
		docker build --pull -t $(IMAGE_NAME):$(IMAGE_VERSION) .
		docker tag $(IMAGE_NAME):$(IMAGE_VERSION) $(IMAGE_NAME):latest

push:
		docker push $(IMAGE_NAME):$(IMAGE_VERSION)
		docker push $(IMAGE_NAME):latest


