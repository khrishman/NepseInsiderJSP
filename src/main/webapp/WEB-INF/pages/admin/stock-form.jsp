<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${stock != null ? 'Edit Stock' : 'Add Stock'} | Admin" scope="request"/>
<jsp:include page="../includes/header.jsp"/>

<h1>${stock != null ? 'Edit Stock' : 'Add New Stock'}</h1>

<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>

<div class="card" style="max-width:720px;">
    <form action="${pageContext.request.contextPath}/admin/stocks" method="post">
        <input type="hidden" name="action" value="${stock != null ? 'update' : 'create'}">
        <c:if test="${stock != null}">
            <input type="hidden" name="id" value="${stock.id}">
        </c:if>

        <div class="form-group">
            <label for="symbol">Symbol *</label>
            <input type="text" id="symbol" name="symbol"
                   value="<c:out value='${stock.symbol}'/>" required maxlength="20"
                   style="text-transform:uppercase;">
        </div>
        <div class="form-group">
            <label for="companyName">Company Name *</label>
            <input type="text" id="companyName" name="companyName"
                   value="<c:out value='${stock.companyName}'/>" required>
        </div>
        <div class="form-group">
            <label for="categoryId">Sector *</label>
            <select id="categoryId" name="categoryId" required>
                <option value="">-- Select Sector --</option>
                <c:forEach var="c" items="${categories}">
                    <option value="${c.id}"
                        <c:if test="${stock.categoryId == c.id}">selected</c:if>>
                        <c:out value="${c.name}"/>
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label for="currentPrice">Current Price (Rs.) *</label>
                <input type="number" step="0.01" id="currentPrice" name="currentPrice"
                       value="${stock.currentPrice}" required>
            </div>
            <div class="form-group">
                <label for="previousPrice">Previous Price (Rs.)</label>
                <input type="number" step="0.01" id="previousPrice" name="previousPrice"
                       value="${stock.previousPrice}">
            </div>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label for="marketCap">Market Cap (Rs.)</label>
                <input type="number" step="0.01" id="marketCap" name="marketCap"
                       value="${stock.marketCap}">
            </div>
            <div class="form-group">
                <label for="volume">Volume</label>
                <input type="number" id="volume" name="volume"
                       value="${stock.volume}">
            </div>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label for="yearListed">Year Listed</label>
                <input type="number" id="yearListed" name="yearListed"
                       value="${stock.yearListed}" min="1937" max="2100">
            </div>
            <div class="form-group">
                <label for="sentiment">Sentiment</label>
                <select id="sentiment" name="sentiment">
                    <option value="NEUTRAL"  <c:if test="${stock.sentiment == 'NEUTRAL'}">selected</c:if>>Neutral</option>
                    <option value="BULLISH"  <c:if test="${stock.sentiment == 'BULLISH'}">selected</c:if>>Bullish</option>
                    <option value="BEARISH"  <c:if test="${stock.sentiment == 'BEARISH'}">selected</c:if>>Bearish</option>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="description">Description</label>
            <textarea id="description" name="description" rows="4"><c:out value="${stock.description}"/></textarea>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">
                ${stock != null ? 'Update Stock' : 'Create Stock'}
            </button>
            <a href="${pageContext.request.contextPath}/admin/stocks" class="btn btn-secondary">Cancel</a>
        </div>
    </form>
</div>

<jsp:include page="../includes/footer.jsp"/>
