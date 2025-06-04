-- Changes links from Markdown to HTML
function Link(el)
    el.target = string.gsub(el.target, "%.md", ".html")
    return el
end

