if [ -f $1 ]; then
  echo "Removing $1"
  docker stop `cat $1` > /dev/null 2>&1 || true
  docker rm `cat $1` > /dev/null 2>&1 || true
  rm $1
fi