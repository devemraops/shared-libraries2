read -p "Enter artifact path: " file_path

if [ -z "$file_path"]; then
  echo "file path must be provided.Exiting"
  exit 1
fi
  curl -u ebasd01:pasw232d -XPOST "https://artifactory.company.thecompany.com/artifactory/api/trash/restore/$file_path?to=$file_path/"