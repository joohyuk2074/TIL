# 스토리지 드라이버

Linux 시스템에서 docker는 이미지, 볼륨 등과 관련된 데이터를 /var/lib/docker에 저장합니다.

<br>

docker build 명령어를 실행하면 docker는 dockerfile의 각 명령에 대해 하나의 레이어를 빌드합니다. 이러한 이미지 레이어는 읽기 전용 레이어 입니다. docker run 명령을 실행하면 docker는 읽기-쓰기 레이어인 컨테이너 레이어를 빌드합니다.
