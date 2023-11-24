import json
import sys
import os

report_data = ""

def generate_report(data):
  print("Parsing report!")

  total_vuln = 0
  if (int(data["packages"]["total"]) != 0):
    report_data = "[Fail] - Sysdig report\n\nListing all vulnerable packages:\n\n"
    report_data += f"{{:<50s}} {{:<15s}} {{:<10s}} {{:<15s}} {{:<18s}} {{:<10s}} {{:<60s}}\n".format("Package Name", \
      "Package Type", \
        "Version", \
          "Fix Version", \
            "CVE", \
              "CVSS Score", \
                "Package Path")
    total_vuln = 0
    fixable_vuln = 0
    for i in range(len(data["package"]["list"])):
      for j in range(len(data["package"]["list"][i]["vulnerabilities"])):
        if (float(data["package"]["List"][i]["vulnerabilities"])[j]["cvssScore"]["value"]["score"] >= 8):
          total_vuln=total_vuln+1
          report_data += f"{{:<50s}} {{:<15s}} {{:<10s}} {{:<15s}} {{:<18s}} {{:<10s}} {{:<60s}}\n".format(data["packages"]["list"][i]["name"], \
            data["packages"]["list"][i]["type"], \
              data["packages"]["list"][i]["version"], \
                str(data["packages"]["list"][i]["suggestedFix"]), \
                  data["packages"]["list"][i]["vulnerabilities"][j]["name"], \
                    float(data["packages"]["list"][i]["vulnerabilities"][j]["cvssScore"]["value"]["score"]), \
                      str(data["packages"]["list"][i]["packagePath"]))
          if data["packages"]["list"][i]["suggestedFix"] is not None: fixable_vuln=fixable_vuln+1
          report_data += f"\n\n-------------------------------------------------------------\nTotal vulnerable packages detected >=8: {total_vuln}\nTotal fixable packages: {fixable_vuln}"
          print(f"Total vulnerable packages detected >= 8: {total_vuln}")
          print(f"Total fixable packages: {fixable_vuln}")
      if total_vuln == 0:
        report_data = "[PASS] - Sysdig has no vulnerabilities with CVSS V3 score\n"
      
      with open(f'{str(os.environ.get("REPORT_NAME")).split("/")[-1]}_report.txt', "w+") as file:
        file.write(report_data)

def main():
# Argv 1 is sysdigreport.json
generate_report(json.loads(open(str(sys.argv[1])).read()))

if __name__ == "__main__":
  main()

# ======================================Second Version of above code=============================================
import json
import os
import sys

def generate_report(data):
    print("Parsing report!")

    total_vuln = 0
    fixable_vuln = 0
    report_data = ""

    if "packages" in data and "total" in data["packages"] and int(data["packages"]["total"]) != 0:
        report_data = "[Fail] - Sysdig report\n\nListing all vulnerable packages:\n\n"
        report_data += "{:<50s} {:<15s} {:<10s} {:<15s} {:<18s} {:<10s} {:<60s}\n".format(
            "Package Name", "Package Type", "Version", "Fix Version", "CVE", "CVSS Score", "Package Path"
        )

        for package in data.get("packages", {}).get("list", []):
            for vulnerability in package.get("vulnerabilities", []):
                cvss_score = float(vulnerability.get("cvssScore", {}).get("value", {}).get("score", 0))

                if cvss_score >= 8:
                    total_vuln += 1
                    report_data += "{:<50s} {:<15s} {:<10s} {:<15s} {:<18s} {:<10s} {:<60s}\n".format(
                        package["name"],
                        package["type"],
                        package["version"],
                        str(package.get("suggestedFix", "")),
                        vulnerability["name"],
                        cvss_score,
                        str(package.get("packagePath", ""))
                    )

                    if package.get("suggestedFix") is not None:
                        fixable_vuln += 1

        report_data += "\n\n-------------------------------------------------------------\n"
        report_data += f"Total vulnerable packages detected >= 8: {total_vuln}\nTotal fixable packages: {fixable_vuln}"

        if total_vuln == 0:
            report_data = "[PASS] - Sysdig has no vulnerabilities with CVSS V3 score\n"

    report_filename = os.path.basename(os.environ.get("REPORT_NAME", ""))
    with open(f"{report_filename}_report.txt", "w+") as file:
        file.write(report_data)

def main():
    if len(sys.argv) != 2:
        print("Usage: python script.py sysdigreport.json")
        sys.exit(1)

    report_file = sys.argv[1]
    with open(report_file, "r") as file:
        report_data = json.load(file)

    generate_report(report_data)

if __name__ == "__main__":
    main()
