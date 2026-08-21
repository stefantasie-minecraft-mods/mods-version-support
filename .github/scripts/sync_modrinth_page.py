"""Brings the Modrinth project page in line with this repository: icon, gallery and description.

mc-publish only uploads versions, so the page itself is kept in sync here.
"""
import json
import mimetypes
import os
import sys
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

API = "https://api.modrinth.com/v2"
USER_AGENT = "stefantasie/mods-version-support (github actions)"

GALLERY = [
    ("docs/overview.png", "Version entries with their result", True),
    ("docs/detail.png", "Which mods are ready for the target version", False),
]
ICON = Path("src/main/resources/assets/mods-version-support/icon.png")
BODY = Path("docs/modrinth-body.md")


def request(method, path, *, query=None, body=None, content_type=None):
    url = f"{API}{path}"
    if query:
        url += "?" + urllib.parse.urlencode(query)
    headers = {"Authorization": TOKEN, "User-Agent": USER_AGENT}
    if content_type:
        headers["Content-Type"] = content_type
    call = urllib.request.Request(url, data=body, headers=headers, method=method)
    try:
        with urllib.request.urlopen(call) as answer:
            payload = answer.read()
            return json.loads(payload) if payload else None
    except urllib.error.HTTPError as failure:
        sys.exit(f"{method} {path} failed with {failure.code}: {failure.read().decode()}")


def upload_icon(project_id):
    request("PATCH", f"/project/{project_id}/icon",
            query={"ext": ICON.suffix.lstrip(".")},
            body=ICON.read_bytes(),
            content_type=mimetypes.guess_type(ICON.name)[0])
    print(f"icon set from {ICON}")


def rebuild_gallery(project_id, existing):
    for image in existing:
        request("DELETE", f"/project/{project_id}/gallery", query={"url": image["url"]})
    for ordering, (path, title, featured) in enumerate(GALLERY):
        picture = Path(path)
        if not picture.exists():
            print(f"skipped missing {path}")
            continue
        request("POST", f"/project/{project_id}/gallery",
                query={"ext": picture.suffix.lstrip("."), "featured": str(featured).lower(),
                       "title": title, "ordering": ordering},
                body=picture.read_bytes(),
                content_type=mimetypes.guess_type(picture.name)[0])
        print(f"gallery image added: {path}")


def update_body(project_id):
    if not BODY.exists():
        print(f"skipped missing {BODY}")
        return
    request("PATCH", f"/project/{project_id}",
            body=json.dumps({"body": BODY.read_text()}).encode(),
            content_type="application/json")
    print(f"description set from {BODY}")


TOKEN = os.environ.get("MODRINTH_TOKEN", "")
PROJECT = os.environ.get("MODRINTH_PROJECT_ID", "")
if not TOKEN or not PROJECT:
    sys.exit("MODRINTH_TOKEN and MODRINTH_PROJECT_ID are required")

project = request("GET", f"/project/{PROJECT}")
print(f"syncing {project['title']} ({project['slug']})")
upload_icon(PROJECT)
rebuild_gallery(PROJECT, project.get("gallery", []))
update_body(PROJECT)
